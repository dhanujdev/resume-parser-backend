package com.resume.tailor.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String startDate;
    private String endDate;
    private String projectUrl;
    @Lob
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // --- Explicit Constructors ---
    public Project() {
        // No-argument constructor
    }

    public Project(Long id, String projectName, String startDate, String endDate, String projectUrl, String description, Candidate candidate) {
        this.id = id;
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectUrl = projectUrl;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
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

    // Exclude bidirectional relationship from lombok methods
    @Override
    public String toString() {
        return "Project{" +
               "id=" + id +
               ", projectName='" + projectName + '\'' +
               ", startDate='" + startDate + '\'' +
               ", endDate='" + endDate + '\'' +
               ", projectUrl='" + projectUrl + '\'' +
               ", description='" + description + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project )) return false;
        return id != null && id.equals(((Project) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 