package com.resume.tailor.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "experience")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String jobTitle;
    private String startDate;
    private String endDate;
    private String location;
    @Lob
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // --- Explicit Constructors ---
    public Experience() {
        // No-argument constructor
    }

    public Experience(Long id, String companyName, String jobTitle, String startDate, String endDate, String location, String description, Candidate candidate) {
        this.id = id;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and Setter for candidate relationship
    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    // Keep custom equals/hashCode/toString
    @Override
    public String toString() {
        return "Experience{" +
               "id=" + id +
               ", companyName='" + companyName + '\'' +
               ", jobTitle='" + jobTitle + '\'' +
               ", startDate='" + startDate + '\'' +
               ", endDate='" + endDate + '\'' +
               ", location='" + location + '\'' +
               ", description='" + description + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Experience )) return false;
        return id != null && id.equals(((Experience) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 