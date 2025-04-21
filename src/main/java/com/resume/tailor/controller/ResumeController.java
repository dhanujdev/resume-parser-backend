package com.resume.tailor.controller;

import com.resume.tailor.entity.Candidate;
import com.resume.tailor.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadAndParseResume(@RequestParam("file") MultipartFile file) {
        log.info("Received request to upload and parse resume: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            log.warn("Upload request received with empty file.");
            return ResponseEntity.badRequest().body("Please select a PDF file to upload.");
        }

        if (!"application/pdf".equals(file.getContentType())) {
             log.warn("Upload request received with non-PDF file type: {}", file.getContentType());
            return ResponseEntity.badRequest().body("Only PDF files are allowed.");
        }

        try {
            Candidate candidate = resumeService.processResume(file);
            log.info("Successfully processed resume, returning candidate ID: {}", candidate.getId());
            // Return the saved Candidate object (or just the ID, or a specific DTO)
            // Returning the full object might expose too much data; consider a CandidateResponseDTO
            return ResponseEntity.ok(candidate);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid argument during resume processing: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            log.error("IO error during resume processing for file {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing PDF file.");
        } catch (RuntimeException e) {
            // Catch broader runtime exceptions (like from Gemini parsing)
            log.error("Runtime error during resume processing for file {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during processing.");
        } catch (Exception e) {
             log.error("Unexpected error during resume processing for file {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected server error occurred.");
        }
    }

    // Optional: Endpoint to get a candidate by ID
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable Long id) {
        log.info("Received request to get candidate by ID: {}", id);
        return resumeService.getCandidateById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                     log.warn("Candidate not found for ID: {}", id);
                     return ResponseEntity.notFound().build();
                });
    }

    // Endpoint to get all candidates
    @GetMapping
    public ResponseEntity<java.util.List<Candidate>> getAllCandidates() {
        log.info("Received request to get all candidates.");
        java.util.List<Candidate> candidates = resumeService.getAllCandidates();
        log.info("Returning {} candidates.", candidates.size());
        return ResponseEntity.ok(candidates);
    }
} 