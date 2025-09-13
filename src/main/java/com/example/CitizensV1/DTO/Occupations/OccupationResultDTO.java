package com.example.CitizensV1.DTO.Occupations;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO class representing a single occupation returned by the /required_skill_recommender API
// It contains the occupation name and its matching score based on the user's skills
public class OccupationResultDTO {

    // The name of the recommended occupation
    @JsonProperty("Occupation")
    private String occupation;

    // The matching score indicating how well the user's skills fit this occupation
    @JsonProperty("Matching")
    private double matching;

    // Default constructor
    public OccupationResultDTO() {}

    // Constructor with occupation name and matching score
    public OccupationResultDTO(String occupation, double matching) {
        this.occupation = occupation;
        this.matching = matching;
    }

    // Getter for occupation name
    public String getOccupation() {
        return occupation;
    }

    // Setter for occupation name
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    // Getter for matching score
    public double getMatching() {
        return matching;
    }

    // Setter for matching score
    public void setMatching(double matching) {
        this.matching = matching;
    }
}
