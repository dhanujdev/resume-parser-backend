package com.resume.tailor.service;

import com.resume.tailor.dto.*;
import com.resume.tailor.entity.*;
import com.resume.tailor.repository.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    // Explicit logger
    private static final Logger log = LoggerFactory.getLogger(ResumeService.class);

    private final PdfParsingService pdfParsingService;
    private final GeminiParsingService geminiParsingService;
    private final CandidateRepository candidateRepository;

    // --- Explicit Constructor for Dependency Injection ---
    public ResumeService(PdfParsingService pdfParsingService, GeminiParsingService geminiParsingService, CandidateRepository candidateRepository) {
        this.pdfParsingService = pdfParsingService;
        this.geminiParsingService = geminiParsingService;
        this.candidateRepository = candidateRepository;
    }

    @Transactional // Ensure all operations are part of a single transaction
    public Candidate processResume(MultipartFile pdfFile) throws IOException {
        log.info("Processing resume file: {}", pdfFile.getOriginalFilename());

        // 1. Parse PDF to Text
        String resumeText = pdfParsingService.parsePdf(pdfFile);
        if (resumeText.isBlank()) {
            log.warn("Extracted text from PDF is blank: {}", pdfFile.getOriginalFilename());
            throw new RuntimeException("Could not extract text from the provided PDF.");
        }
        log.debug("Extracted resume text (first 100 chars): {}", resumeText.substring(0, Math.min(resumeText.length(), 100)));

        // 2. Parse Sections using Gemini
        // Use the appropriate parseSection or parseListSection method
        CandidateDTO candidateDTO = geminiParsingService.parseSection(resumeText, "Candidate Contact Info", CandidateDTO.class);
        List<EducationDTO> educationDTOs = geminiParsingService.parseListSection(resumeText, "Education", EducationDTO.class);
        List<ExperienceDTO> experienceDTOs = geminiParsingService.parseListSection(resumeText, "Work Experience", ExperienceDTO.class);
        List<SkillDTO> skillDTOs = geminiParsingService.parseListSection(resumeText, "Skills", SkillDTO.class);
        List<ProjectDTO> projectDTOs = geminiParsingService.parseListSection(resumeText, "Projects", ProjectDTO.class);
        SummaryDTO summaryDTO = geminiParsingService.parseSection(resumeText, "Summary or Objective", SummaryDTO.class);

        // 3. Map DTOs to Entities and Save
        Candidate candidate = mapAndSaveCandidate(candidateDTO, educationDTOs, experienceDTOs, skillDTOs, projectDTOs, summaryDTO);

        log.info("Successfully processed and saved resume for candidate ID: {}", candidate.getId());
        return candidate;
    }

    // Helper method to handle potential null DTOs and map to entities
    private Candidate mapAndSaveCandidate(CandidateDTO candidateDTO,
                                        List<EducationDTO> educationDTOs,
                                        List<ExperienceDTO> experienceDTOs,
                                        List<SkillDTO> skillDTOs,
                                        List<ProjectDTO> projectDTOs,
                                        SummaryDTO summaryDTO) {
        Candidate candidate = new Candidate();

        // Map basic candidate info
        if (candidateDTO != null) {
            candidate.setFullName(candidateDTO.getFullName());
            candidate.setEmail(candidateDTO.getEmail());
            candidate.setPhoneNumber(candidateDTO.getPhoneNumber());
            candidate.setAddress(candidateDTO.getAddress());
            candidate.setLinkedinUrl(candidateDTO.getLinkedinUrl());
        } else {
            log.warn("CandidateDTO received from Gemini was null. Basic info might be missing.");
            // Handle this case - maybe throw an error or create a placeholder?
        }

        // Map education
        if (educationDTOs != null && !educationDTOs.isEmpty()) {
            educationDTOs.stream()
                    .map(this::mapToEducationEntity)
                    .forEach(candidate::addEducation); // Use helper method for bidirectional link
        }

        // Map experience
        if (experienceDTOs != null && !experienceDTOs.isEmpty()) {
            experienceDTOs.stream()
                    .map(this::mapToExperienceEntity)
                    .forEach(candidate::addExperience);
        }

        // Map skills
        if (skillDTOs != null && !skillDTOs.isEmpty()) {
            skillDTOs.stream()
                    .map(this::mapToSkillEntity)
                    .forEach(candidate::addSkill);
        }

        // Map projects
        if (projectDTOs != null && !projectDTOs.isEmpty()) {
            projectDTOs.stream()
                    .map(this::mapToProjectEntity)
                    .forEach(candidate::addProject);
        }

        // Map summary
        if (summaryDTO != null && summaryDTO.getContent() != null && !summaryDTO.getContent().isBlank()) {
            Summary summary = new Summary();
            summary.setContent(summaryDTO.getContent());
            candidate.setSummary(summary); // Use helper method for bidirectional link
        }

        return candidateRepository.save(candidate);
    }

    // --- Private Mapping Methods --- //

    private Education mapToEducationEntity(EducationDTO dto) {
        Education entity = new Education();
        entity.setInstitutionName(dto.getInstitutionName());
        entity.setDegree(dto.getDegree());
        entity.setFieldOfStudy(dto.getFieldOfStudy());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setGpa(dto.getGpa());
        entity.setDescription(dto.getDescription());
        // The candidate relationship is set by the helper method addEducation()
        return entity;
    }

    private Experience mapToExperienceEntity(ExperienceDTO dto) {
        Experience entity = new Experience();
        entity.setCompanyName(dto.getCompanyName());
        entity.setJobTitle(dto.getJobTitle());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setLocation(dto.getLocation());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private Skill mapToSkillEntity(SkillDTO dto) {
        Skill entity = new Skill();
        entity.setSkillName(dto.getSkillName());
        entity.setProficiencyLevel(dto.getProficiencyLevel());
        return entity;
    }

    private Project mapToProjectEntity(ProjectDTO dto) {
        Project entity = new Project();
        entity.setProjectName(dto.getProjectName());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setProjectUrl(dto.getProjectUrl());
        entity.setDescription(dto.getDescription());
        return entity;
    }

     // Add a method to fetch a candidate by ID (optional, for future use)
    @Transactional(readOnly = true)
    public Optional<Candidate> getCandidateById(Long id) {
        log.debug("Fetching candidate by ID: {}", id);
        // Use findById to eagerly fetch collections if needed, or rely on default lazy loading
        return candidateRepository.findById(id);
    }

    // Method to fetch all candidates
    @Transactional(readOnly = true)
    public java.util.List<Candidate> getAllCandidates() {
        log.debug("Fetching all candidates.");
        return candidateRepository.findAll();
    }
} 