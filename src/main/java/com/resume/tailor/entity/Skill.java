package com.resume.tailor.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;
    private String proficiencyLevel; // e.g., Beginner, Intermediate, Advanced or Years of Experience

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // --- Explicit Constructors ---
    public Skill() {
        // No-argument constructor
    }

    public Skill(Long id, String skillName, String proficiencyLevel, Candidate candidate) {
        this.id = id;
        this.skillName = skillName;
        this.proficiencyLevel = proficiencyLevel;
        this.candidate = candidate;
    }

    // --- Explicit Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    // Exclude bidirectional relationship from lombok methods
    @Override
    public String toString() {
        return "Skill{" +
               "id=" + id +
               ", skillName='" + skillName + '\'' +
               ", proficiencyLevel='" + proficiencyLevel + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill )) return false;
        return id != null && id.equals(((Skill) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 