package com.resume.tailor.repository;

import com.resume.tailor.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    // Optional: Add custom query methods if needed, e.g.:
    // Optional<Candidate> findByEmail(String email);
} 