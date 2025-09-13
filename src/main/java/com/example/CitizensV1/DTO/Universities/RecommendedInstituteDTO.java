package com.example.CitizensV1.DTO.Universities;

import java.util.List;

// DTO class representing a recommended university/institute
// It carries the name, coverage percentage, missing skills, and offered skills of an institute
public class RecommendedInstituteDTO {

    // Name of the university or institute
    private String university;

    // Coverage percentage of the institute over the missing skills (0-100)
    private Double coverage;

    // List of missing skills that this institute does not cover
    private List<String> missingSkills;

    // List of skills that this institute does offer (covered skills)
    private List<String> offeredSkills;

    // Constructor for initializing the recommended institute with name, coverage, missing skills, and offered skills
    public RecommendedInstituteDTO(String university, Double coverage, List<String> missingSkills, List<String> offeredSkills) {
        this.university = university;
        this.coverage = coverage;
        this.missingSkills = missingSkills;
        this.offeredSkills = offeredSkills;
    }

    // Getter for university
    public String getUniversity() {
        return university;
    }

    // Getter for coverage
    public double getCoverage() {
        return coverage;
    }

    // Getter for missingSkills
    public List<String> getMissingSkills() {
        return missingSkills;
    }

    // Getter for offeredSkills
    public List<String> getOfferedSkills() {
        return offeredSkills;
    }
}
