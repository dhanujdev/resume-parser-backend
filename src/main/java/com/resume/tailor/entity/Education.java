package com.resume.tailor.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "education")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String institutionName;
    private String degree;
    private String fieldOfStudy;
    private String startDate; // Consider using LocalDate or String based on expected format
    private String endDate;
    private String gpa;
    @Lob // Use @Lob for potentially large text fields
    @Column(length = 1000) // Specify length if needed
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // --- Explicit Constructors ---
    public Education() {
        // No-argument constructor
    }

    public Education(Long id, String institutionName, String degree, String fieldOfStudy, String startDate, String endDate, String gpa, String description, Candidate candidate) {
        this.id = id;
        this.institutionName = institutionName;
        this.degree = degree;
        this.fieldOfStudy = fieldOfStudy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.gpa = gpa;
        this.description = description;
        this.candidate = candidate;
    }

    // --- Explicit Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    // Exclude bidirectional relationship from lombok's toString to avoid recursion
    @Override
    public String toString() {
        return "Education{" +
               "id=" + id +
               ", institutionName='" + institutionName + '\'' +
               ", degree='" + degree + '\'' +
               ", fieldOfStudy='" + fieldOfStudy + '\'' +
               ", startDate='" + startDate + '\'' +
               ", endDate='" + endDate + '\'' +
               ", gpa='" + gpa + '\'' +
               ", description='" + description + '\'' +
               '}';
    }

     // Exclude bidirectional relationship from lombok's equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Education )) return false;
        return id != null && id.equals(((Education) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 