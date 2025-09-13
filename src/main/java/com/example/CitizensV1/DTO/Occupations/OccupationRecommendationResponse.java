package com.example.CitizensV1.DTO.Occupations;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

// DTO class representing the response body for the /required_skill_recommender endpoint
// It carries the occupations recommended based on the user’s skills and the matched skills per occupation
public class OccupationRecommendationResponse {

    // List of recommended occupations with matching score
    @JsonProperty("Results")
    private List<OccupationResultDTO> results;

    // Map of occupation names to the list of skills that matched the user's input
    @JsonProperty("Skills")
    private Map<String, List<String>> skills;

    // Default constructor
    public OccupationRecommendationResponse() {}

    // Constructor with results and skills
    public OccupationRecommendationResponse(List<OccupationResultDTO> results,
                                            Map<String, List<String>> skills) {
        this.results = results;
        this.skills = skills;
    }

    // Getter for results
    public List<OccupationResultDTO> getResults() {
        return results;
    }

    // Setter for results
    public void setResults(List<OccupationResultDTO> results) {
        this.results = results;
    }

    // Getter for skills
    public Map<String, List<String>> getSkills() {
        return skills;
    }

    // Setter for skills
    public void setSkills(Map<String, List<String>> skills) {
        this.skills = skills;
    }
}
