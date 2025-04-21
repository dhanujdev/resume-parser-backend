package com.resume.tailor.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "summary")
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(length = 4000) // Allow ample space for summary text
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", referencedColumnName = "id", nullable = false)
    private Candidate candidate;

    // --- Explicit Constructors ---
    public Summary() {
        // No-argument constructor
    }

    public Summary(Long id, String content, Candidate candidate) {
        this.id = id;
        this.content = content;
        this.candidate = candidate;
    }

    // --- Explicit Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        return "Summary{" +
               "id=" + id +
               ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 50)) + "..." : null) + '\'' + // Truncate long content
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Summary )) return false;
        return id != null && id.equals(((Summary) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 