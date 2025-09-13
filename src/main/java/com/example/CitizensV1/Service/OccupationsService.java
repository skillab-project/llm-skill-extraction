package com.example.CitizensV1.Service;

import com.example.CitizensV1.DTO.Occupations.OccupationRecommendationRequest;
import com.example.CitizensV1.DTO.Occupations.OccupationRecommendationResponse;
import com.example.CitizensV1.DTO.Occupations.OccupationResultDTO;
import com.example.CitizensV1.Model.Citizens.CitizenSkill;
import com.example.CitizensV1.Model.Occupations.Occupation;
import com.example.CitizensV1.Model.Occupations.OccupationSkill;
import com.example.CitizensV1.Repository.CitizenSkillRepository;
import com.example.CitizensV1.Repository.OccupationsRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OccupationsService manages operations related to occupations.
 * It provides:
 * - Retrieving all occupations
 * - Getting details of an occupation
 * - Recommending occupations based on citizen skills
 * - CRUD operations (create, bulk create, delete)
 */

@Service
public class OccupationsService {

    private final OccupationsRepository occupationsRepository;
    private final CitizenSkillRepository citizenSkillRepository;
    private RestTemplate restTemplate;
    private static final String BASE_URL = "https://portal.skillab-project.eu/required-skills/required_skill_recommender?matching_number=";

    public OccupationsService(OccupationsRepository occupationsRepository, CitizenSkillRepository citizenSkillRepository) {
        this.occupationsRepository = occupationsRepository;
        this.citizenSkillRepository = citizenSkillRepository;
        this.restTemplate = new RestTemplate();
    }

    //-------------------------------GET METHODS--------------------------------
    //-------------------------------OCCUPATIONS--------------------------------

    // Returns all occupations
    public List<Occupation> getOccupations() {
        return occupationsRepository.findAll();
    }

    // Returns details of an occupation by name
    public ResponseEntity<?> getOccupationDetails(String occupationName) {
        Optional<Occupation> optionalOccupation = occupationsRepository.findByName(occupationName);

        if (optionalOccupation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Occupation not found"));
        }

        Occupation occupation = optionalOccupation.get();

        List<Map<String, Object>> skills = occupation.getRequiredSkills().stream()
                .map(os -> {
                    Map<String, Object> skillMap = new HashMap<>();
                    skillMap.put("skillName", os.getSkill().getTitle());
                    skillMap.put("requiredLevel", os.getLevel());
                    return skillMap;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("skills", skills);

        return ResponseEntity.ok(response);
    }

    // Returns recommended occupations for a citizen based on their skills
    //ITS IN PYTHON MIGHT DELET LATER
    public List<Map<String, Object>> getRecommendedOccupations(Long citizenId) {
        Map<String, Integer> citizenSkills = new HashMap<>();
        List<CitizenSkill> skills = citizenSkillRepository.findByCitizenId(citizenId);
        for (CitizenSkill cs : skills) {
            citizenSkills.put(cs.getSkill().getTitle(), cs.getLevel());
        }

        List<Occupation> occupations = occupationsRepository.findAll();
        List<Map<String, Object>> recommendations = new ArrayList<>();

        for (Occupation occ : occupations) {
            List<OccupationSkill> requiredSkills = occ.getRequiredSkills();
            if (requiredSkills.isEmpty()) continue;

            int matchCount = 0;
            List<String> missingSkills = new ArrayList<>();
            for (OccupationSkill os : requiredSkills) {
                String skillName = os.getSkill().getTitle();
                int requiredLevel = os.getLevel();
                int citizenLevel = citizenSkills.getOrDefault(skillName, 0);
                if (citizenLevel >= requiredLevel) {
                    matchCount++;
                } else {
                    missingSkills.add(skillName);
                }
            }

            if (matchCount > 0) {
                double matchPercentage = (double) matchCount / requiredSkills.size() * 100;
                Map<String, Object> rec = new HashMap<>();
                rec.put("occupationName", occ.getName());
                rec.put("matchPercentage", Math.round(matchPercentage * 100.0)/100.0);
                rec.put("missingSkills", missingSkills);
                recommendations.add(rec);
            }
        }

        recommendations.sort((a, b) -> Double.compare((double) b.get("matchPercentage"), (double) a.get("matchPercentage")));
        return recommendations;
    }

    //-------------------------------POST METHODS-------------------------------
    //-------------------------------OCCUPATIONS--------------------------------

    // Adds a new occupation
    public void addNewOccupation(Occupation occupation) {
        Optional<Occupation> optional = occupationsRepository.findByName(occupation.getName());
        if (optional.isPresent()) {
            throw new IllegalStateException("Occupation with name " + occupation.getName() + " already exists");
        }
        occupationsRepository.save(occupation);
    }

    // Adds multiple occupations in bulk
    public void addNewOccupations(List<Occupation> occupations) {
        List<Occupation> uniqueOccupations = occupations.stream()
                .filter(o -> !occupationsRepository.findByName(o.getName()).isPresent())
                .toList();

        if (!uniqueOccupations.isEmpty()) {
            occupationsRepository.saveAll(uniqueOccupations);
        }
    }

    //-------------------------------RECOMMENDED OCCUPATIONS-------------------
    //-------------------------------SKILLS------------------------------------

    public OccupationRecommendationResponse recommendOccupations(List<String> skillList, int matchingNumber, double threshold) {
         String url = BASE_URL + matchingNumber;

        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("skill_list", skillList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Call external API
        OccupationRecommendationResponse response =
                restTemplate.postForObject(url, requestEntity, OccupationRecommendationResponse.class);

        if (response == null || response.getResults() == null) {
            throw new RuntimeException("No occupations found from external API");
        }

        // --- Apply threshold filter ---
        List<OccupationResultDTO> filteredResults = response.getResults().stream()
                .filter(r -> r.getMatching() >= threshold) // keep only occupations above threshold
                .toList();

        // Build new response with filtered results
        OccupationRecommendationResponse filteredResponse = new OccupationRecommendationResponse();
        filteredResponse.setResults(filteredResults);

        // Keep only skills of the filtered occupations
        if (response.getSkills() != null) {
            Map<String, List<String>> filteredSkills = response.getSkills().entrySet().stream()
                    .filter(entry -> filteredResults.stream()
                            .anyMatch(r -> r.getOccupation().equals(entry.getKey())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            filteredResponse.setSkills(filteredSkills);
        }

        return filteredResponse;
    }

    //-------------------------------DELETE METHODS-----------------------------
    //-------------------------------OCCUPATIONS--------------------------------

    // Deletes an occupation by id
    public void deleteOccupation(Long occupationId) {
        Optional<Occupation> optional = occupationsRepository.findById(occupationId);
        if (!optional.isPresent()) {
            throw new IllegalStateException("Occupation with id " + occupationId + " does not exist");
        }
        occupationsRepository.deleteById(occupationId);
    }

}
