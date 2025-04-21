package com.resume.tailor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.aiplatform.v1.*;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class GeminiParsingService {

    private static final Logger log = LoggerFactory.getLogger(GeminiParsingService.class);

    private final VertexAI vertexAI;
    private final String projectId;
    private final String location;
    private final ObjectMapper objectMapper;

    private String modelName;
    private ModelServiceClient modelServiceClient;

    public GeminiParsingService(
            VertexAI vertexAI,
            @Value("${spring.ai.vertex.ai.gemini.project-id}") String projectId,
            @Value("${spring.ai.vertex.ai.gemini.location}") String location) {
        this.vertexAI = vertexAI;
        this.projectId = projectId;
        this.location = location;
        this.objectMapper = new ObjectMapper();
        try {
            String endpoint = String.format("%s-aiplatform.googleapis.com:443", this.location);
            ModelServiceSettings modelServiceSettings =
                    ModelServiceSettings.newBuilder().setEndpoint(endpoint).build();
            this.modelServiceClient = ModelServiceClient.create(modelServiceSettings);
        } catch (IOException e) {
            log.error("Failed to create ModelServiceClient for location {}: {}", this.location, e.getMessage(), e);
            throw new RuntimeException("Could not initialize ModelServiceClient", e);
        }

        log.info("GeminiParsingService partially initialized. Project ID: {}, Location: {}. Model discovery pending.", projectId, location);
    }

    public String getModelName() {
        return modelName;
    }

    @PostConstruct
    private void discoverAndSetModel() {
        log.info("Attempting to discover Gemini models...");
        try {
            LocationName parent = LocationName.of(projectId, location);
            ListModelsRequest request = ListModelsRequest.newBuilder()
                    .setParent(parent.toString())
                    .build();

            ModelServiceClient.ListModelsPagedResponse response = modelServiceClient.listModels(request);

            Optional<Model> selectedModel = StreamSupport.stream(response.iterateAll().spliterator(), false)
                    .filter(model -> model.getDisplayName().toLowerCase().contains("gemini"))
                    .min(Comparator.comparing((Model model) -> {
                        String nameLower = model.getDisplayName().toLowerCase();
                        if (nameLower.contains("flash")) return 0;
                        if (nameLower.contains("pro")) return 1;
                        return 2;
                    }));

            if (selectedModel.isPresent()) {
                String fullModelName = selectedModel.get().getName();
                this.modelName = fullModelName.substring(fullModelName.lastIndexOf('/') + 1);
                log.info("Successfully discovered and selected Gemini model: {}", this.modelName);
            } else {
                log.error("No Gemini models found in project '{}' location '{}'. Falling back to default.", projectId, location);
                this.modelName = "gemini-1.5-flash-001";
                log.warn("Falling back to default model: {}", this.modelName);
            }

        } catch (Exception e) {
            log.error("Failed to discover models: {}. Falling back to default.", e.getMessage(), e);
            this.modelName = "gemini-1.5-flash-001";
            log.warn("Falling back to default model due to error: {}", this.modelName);
        } finally {
            if (this.modelServiceClient != null) {
                this.modelServiceClient.close();
                log.debug("ModelServiceClient closed.");
            }
        }
        log.info("GeminiParsingService fully initialized with dynamically selected Model: {}", this.modelName);
    }

    public <T> T parseSection(String resumeText, String sectionName, Class<T> responseType) {
        if (this.modelName == null || this.modelName.isBlank()) {
            log.error("Gemini model name is not set. Cannot process section '{}'.", sectionName);
            throw new IllegalStateException("Gemini model name has not been initialized.");
        }
        String prompt = buildPrompt(resumeText, sectionName, responseType);
        String rawJsonResponse = callGeminiApi(prompt, sectionName);
        String cleanedJson = cleanJsonResponse(rawJsonResponse);
        return parseJson(cleanedJson, responseType, sectionName);
    }

    public <T> List<T> parseListSection(String resumeText, String sectionName, Class<T> elementType) {
        if (this.modelName == null || this.modelName.isBlank()) {
            log.error("Gemini model name is not set. Cannot process list section '{}'.", sectionName);
            throw new IllegalStateException("Gemini model name has not been initialized.");
        }
        TypeReference<List<T>> listTypeReference = new TypeReference<List<T>>() {};
        String prompt = buildListPrompt(resumeText, sectionName, elementType);
        String rawJsonResponse = callGeminiApi(prompt, sectionName);
        String cleanedJson = cleanJsonResponse(rawJsonResponse);
        return parseJsonList(cleanedJson, listTypeReference, sectionName);
    }

    private String callGeminiApi(String prompt, String sectionName) {
        log.debug("Sending prompt to Gemini model '{}' for section '{}':\n{}", this.modelName, sectionName, prompt);
        try {
            if (this.vertexAI == null) {
                log.error("VertexAI client is null. Cannot call Gemini API.");
                throw new IllegalStateException("VertexAI client has not been initialized.");
            }
            GenerativeModel model = new GenerativeModel(this.modelName, this.vertexAI);
            GenerateContentResponse response = model.generateContent(prompt);
            String jsonResponse = ResponseHandler.getText(response);
            log.debug("Received raw JSON response from Gemini for section '{}':\n{}", sectionName, jsonResponse);
            return jsonResponse;
        } catch (IOException e) {
            log.error("IOException while communicating with Gemini API for section '{}': {}", sectionName, e.getMessage(), e);
            throw new RuntimeException("Error communicating with Gemini API", e);
        } catch (Exception e) {
            log.error("Unexpected error during Gemini API call for section '{}' using model '{}': {}", sectionName, this.modelName, e.getMessage(), e);
            throw new RuntimeException("Unexpected error calling Gemini API", e);
        }
    }

    private <T> String buildPrompt(String resumeText, String sectionName, Class<T> responseType) {
        String formatInstructions = getJsonFormatInstructions(responseType);
        return buildPromptInternal(resumeText, sectionName, formatInstructions);
    }

    private <T> String buildListPrompt(String resumeText, String sectionName, Class<T> elementType) {
        String formatInstructions = getJsonFormatInstructionsForList(elementType);
        return buildPromptInternal(resumeText, sectionName, formatInstructions);
    }

    private String buildPromptInternal(String resumeText, String sectionName, String formatInstructions) {
        String promptTemplate =
                "Extract the %s information from the following resume text. " +
                "Format the output strictly as a JSON matching the structure below. " +
                "For lists, provide a JSON array of objects. For single objects, provide a JSON object. " +
                "If a field or section is not found, use null or an empty list/string as appropriate for the expected JSON structure. " +
                "Do NOT include any introductory text, explanations, or markdown formatting like ```json. " +
                "Only provide the JSON array or object.\\n\\n" +
                "JSON Structure:\\n%s\\n\\n" +
                "Resume Text:\\n```\\n%s\\n```\\n\\n" +
                "JSON Output:";

        return String.format(promptTemplate, sectionName, formatInstructions, resumeText);
    }

    private String cleanJsonResponse(String rawResponse) {
        String cleaned = rawResponse.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }
        if (cleaned.isBlank() || cleaned.equalsIgnoreCase("null")) {
            return null;
        }
        return cleaned;
    }

    private <T> T parseJson(String jsonResponse, Class<T> responseType, String sectionName) {
        if (jsonResponse == null) {
            log.warn("Received null or empty JSON response from Gemini for section '{}'. Returning null.", sectionName);
            return null;
        }
        try {
            return objectMapper.readValue(jsonResponse, responseType);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON object for section '{}'. Response was:\n{}", sectionName, jsonResponse, e);
            throw new RuntimeException("Failed to parse Gemini JSON object for section: " + sectionName, e);
        }
    }

    private <T> List<T> parseJsonList(String jsonResponse, TypeReference<List<T>> typeReference, String sectionName) {
        if (jsonResponse == null) {
            log.warn("Received null or empty JSON response from Gemini for list section '{}'. Returning empty list.", sectionName);
            return List.of();
        }
        try {
            return objectMapper.readValue(jsonResponse, typeReference);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON list for section '{}'. Response was:\n{}", sectionName, jsonResponse, e);
            return List.of();
        }
    }

    private <T> String getJsonFormatInstructions(Class<T> responseType) {
        try {
            T instance = responseType.getDeclaredConstructor().newInstance();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(instance);
        } catch (Exception e) {
            log.warn("Could not generate JSON format instructions for type {}: {}", responseType.getSimpleName(), e.getMessage());
            return "{\"error\": \"Could not generate format example\"}";
        }
    }

    private <T> String getJsonFormatInstructionsForList(Class<T> elementType) {
        try {
            T instance = elementType.getDeclaredConstructor().newInstance();
            List<T> listInstance = List.of(instance);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(listInstance);
        } catch (Exception e) {
            log.warn("Could not generate JSON format instructions for list of type {}: {}", elementType.getSimpleName(), e.getMessage());
            return "[{\"error\": \"Could not generate list format example\"}]";
        }
    }
} 