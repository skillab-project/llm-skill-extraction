package com.example.CitizensV1.DTO.Occupations;

import java.util.List;

// DTO class representing the request body for the /required_skill_recommender endpoint
// It carries the input skills from the client to the service layer
public class OccupationRecommendationRequest {

    // List of skills that the user already possesses
    private List<String> skill_list;

    // Default constructor
    public OccupationRecommendationRequest() {}

    // Constructor with skill list
    public OccupationRecommendationRequest(List<String> skill_list) {
        this.skill_list = skill_list;
    }

    // Getter for skill_list
    public List<String> getSkill_list() {
        return skill_list;
    }

    // Setter for skill_list
    public void setSkill_list(List<String> skill_list) {
        this.skill_list = skill_list;
    }
}
