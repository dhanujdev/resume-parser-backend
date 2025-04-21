package com.resume.tailor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillDTO {
    private String skillName;
    private String proficiencyLevel; // Or yearsOfExperience, adjust based on Gemini's likely output

    // --- Explicit Constructor ---
    public SkillDTO() {
        // No-argument constructor
    }

    // --- Explicit Getters and Setters ---

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }
} 