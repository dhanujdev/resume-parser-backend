package com.resume.tailor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryDTO {
    private String content;

    // --- Explicit Constructor ---
    public SummaryDTO() {
        // No-argument constructor
    }

    // --- Explicit Getters and Setters ---

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
} 