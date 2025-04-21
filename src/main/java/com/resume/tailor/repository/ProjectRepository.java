package com.resume.tailor.repository;

import com.resume.tailor.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Optional: Find all project entries for a specific candidate
    // List<Project> findByCandidateId(Long candidateId);
} 