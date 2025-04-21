package com.resume.tailor.repository;

import com.resume.tailor.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {
    // Optional: Find the summary for a specific candidate
    // Optional<Summary> findByCandidateId(Long candidateId);
} 