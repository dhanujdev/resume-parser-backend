package com.resume.tailor.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String linkedinUrl;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Education> educationList = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Experience> experienceList = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Summary summary;

    public Candidate() {
        // No-argument constructor
    }

    public Candidate(Long id, String fullName, String email, String phoneNumber, String address, String linkedinUrl, List<Education> educationList, List<Experience> experienceList, List<Skill> skills, List<Project> projects, Summary summary) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.linkedinUrl = linkedinUrl;
        this.educationList = educationList != null ? educationList : new ArrayList<>();
        this.experienceList = experienceList != null ? experienceList : new ArrayList<>();
        this.skills = skills != null ? skills : new ArrayList<>();
        this.projects = projects != null ? projects : new ArrayList<>();
        this.summary = summary;
        if (this.educationList != null) this.educationList.forEach(edu -> edu.setCandidate(this));
        if (this.experienceList != null) this.experienceList.forEach(exp -> exp.setCandidate(this));
        if (this.skills != null) this.skills.forEach(skill -> skill.setCandidate(this));
        if (this.projects != null) this.projects.forEach(proj -> proj.setCandidate(this));
        if (this.summary != null) this.summary.setCandidate(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public List<Education> getEducationList() {
        return educationList;
    }

    public void setEducationList(List<Education> educationList) {
        if (this.educationList != null) {
            this.educationList.forEach(edu -> edu.setCandidate(null));
        }
        this.educationList = educationList != null ? educationList : new ArrayList<>();
        if (this.educationList != null) {
             this.educationList.forEach(edu -> edu.setCandidate(this));
        }
    }

    public List<Experience> getExperienceList() {
        return experienceList;
    }

    public void setExperienceList(List<Experience> experienceList) {
       if (this.experienceList != null) {
            this.experienceList.forEach(exp -> exp.setCandidate(null));
        }
        this.experienceList = experienceList != null ? experienceList : new ArrayList<>();
        if (this.experienceList != null) {
             this.experienceList.forEach(exp -> exp.setCandidate(this));
        }
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        if (this.skills != null) {
            this.skills.forEach(skill -> skill.setCandidate(null));
        }
        this.skills = skills != null ? skills : new ArrayList<>();
        if (this.skills != null) {
             this.skills.forEach(skill -> skill.setCandidate(this));
        }
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        if (this.projects != null) {
            this.projects.forEach(proj -> proj.setCandidate(null));
        }
        this.projects = projects != null ? projects : new ArrayList<>();
        if (this.projects != null) {
             this.projects.forEach(proj -> proj.setCandidate(this));
        }
    }

    public Summary getSummary() {
        return summary;
    }

    public void addEducation(Education education) {
        educationList.add(education);
        education.setCandidate(this);
    }

    public void removeEducation(Education education) {
        educationList.remove(education);
        education.setCandidate(null);
    }

    public void addExperience(Experience experience) {
        experienceList.add(experience);
        experience.setCandidate(this);
    }

    public void removeExperience(Experience experience) {
        experienceList.remove(experience);
        experience.setCandidate(null);
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.setCandidate(this);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
        skill.setCandidate(null);
    }

    public void addProject(Project project) {
        projects.add(project);
        project.setCandidate(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.setCandidate(null);
    }

    public void setSummary(Summary summary) {
        if (summary == null) {
            if (this.summary != null) {
                this.summary.setCandidate(null);
            }
        } else {
            summary.setCandidate(this);
        }
        this.summary = summary;
    }
} 