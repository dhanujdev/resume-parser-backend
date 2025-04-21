package com.resume.tailor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Ignore unknown properties coming from Gemini if the prompt isn't perfect
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandidateDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String linkedinUrl;

    // --- Explicit Constructor ---
    public CandidateDTO() {
        // No-argument constructor
    }

    // --- Explicit Getters and Setters ---

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }
} 