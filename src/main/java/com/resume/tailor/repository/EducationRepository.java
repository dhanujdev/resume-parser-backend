package com.resume.tailor.repository;

import com.resume.tailor.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    // Optional: Find all education entries for a specific candidate
    // List<Education> findByCandidateId(Long candidateId);
} 