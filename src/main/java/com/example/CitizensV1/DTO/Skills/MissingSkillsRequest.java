package com.example.CitizensV1.DTO.Skills;

import java.util.List;

// DTO class representing the request body for the /missingSkills endpoint
// It carries the input data from the client to the service layer
public class MissingSkillsRequest {

    // The name of the occupation (role) to check required skills for
    private String occupationName;

    // List of skills that the citizen already has
    private List<String> citizenSkills;

    // Minimum value threshold for skills to be considered
    private double minValue ;

    // Default constructor
    public MissingSkillsRequest() {}

    // Getter for occupationName
    public String getOccupationName() {
        return occupationName;
    }

    // Setter for occupationName
    public void setOccupationName(String occupationName) {
        this.occupationName = occupationName;
    }

    // Getter for citizenSkills
    public List<String> getCitizenSkills() {
        return citizenSkills;
    }

    // Setter for citizenSkills
    public void setCitizenSkills(List<String> citizenSkills) {
        this.citizenSkills = citizenSkills;
    }

    // Getter for minValue
    public double getMinValue() {
        return minValue;
    }

    // Setter for minValue
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }
}
