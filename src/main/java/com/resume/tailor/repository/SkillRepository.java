package com.resume.tailor.repository;

import com.resume.tailor.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    // Optional: Find all skill entries for a specific candidate
    // List<Skill> findByCandidateId(Long candidateId);
} 