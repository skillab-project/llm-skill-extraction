package com.example.CitizensV1.DTO.Universities;

import java.util.List;

// DTO class representing the response body for the /recommend_universities endpoint
// It carries the output data from the service layer back to the client
public class InstituteRecommendationResponse {

    // The list of requested (missing) skills required for the target occupation
    private List<String> requestedSkills;

    // List of recommended universities/institutes with their coverage and uncovered skills
    private List<RecommendedInstituteDTO> recommendedUniversities;

    // Constructor for initializing the response with requested skills and recommended universities
    public InstituteRecommendationResponse(List<String> requestedSkills, List<RecommendedInstituteDTO> recommendedUniversities) {
        this.requestedSkills = requestedSkills;
        this.recommendedUniversities = recommendedUniversities;
    }

    // Getter for requestedSkills
    public List<String> getRequestedSkills() {
        return requestedSkills;
    }

    // Getter for recommendedUniversities
    public List<RecommendedInstituteDTO> getRecommendedUniversities() {
        return recommendedUniversities;
    }
}
