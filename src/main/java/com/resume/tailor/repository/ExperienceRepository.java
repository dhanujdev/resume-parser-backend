package com.resume.tailor.repository;

import com.resume.tailor.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    // Optional: Find all experience entries for a specific candidate
    // List<Experience> findByCandidateId(Long candidateId);
} 