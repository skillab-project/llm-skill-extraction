package com.example.CitizensV1.Service;

import com.example.CitizensV1.DTO.Skills.MissingSkillDTO;
import com.example.CitizensV1.DTO.Skills.RequiredSkillResponse;
import com.example.CitizensV1.Model.Skill;
import com.example.CitizensV1.Repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing Skills.
 * Provides methods to retrieve, add, and bulk add skills in the system.
 */
@Service
public class SkillService {

    private SkillRepository skillRepository;
    private static final String REQUIRED_SKILLS_URL = "https://portal.skillab-project.eu/required-skills/required_skills_service";
    private final RestTemplate restTemplate;


    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
        this.restTemplate = new RestTemplate();
    }

    //-------------------------------GET METHODS--------------------------------
    //-------------------------------SKILLS-------------------------------------

    // Returns a list of all skills from the database
    public List<Skill> getSkills() {
        return skillRepository.findAll();
    }

    //-------------------------------POST METHODS-------------------------------
    //-------------------------------SKILLS-------------------------------------

    // Adds a new skill
    public void addNewSkill(Skill skill) {
        Optional<Skill> optionalSkill = skillRepository.findByTitle(skill.getTitle());
        if (optionalSkill.isPresent()) {
            throw new IllegalStateException("Skill with title " + skill.getTitle() + " already exists");
        }
        skillRepository.save(skill);
    }

    // Adds multiple skills in bulk
    public void addNewSkills(List<Skill> skills) {
        List<Skill> uniqueSkills = skills.stream()
                .filter(s -> skillRepository.findByTitle(s.getTitle()).isEmpty())
                .collect(Collectors.toList());

        if (!uniqueSkills.isEmpty()) {
            skillRepository.saveAll(uniqueSkills);
        }
    }

    // Finds the missing skills of a citizen
    public List<MissingSkillDTO> findMissingSkills(String occupationName, List<String> citizenSkills, Double minValue) {

        // The URL of the external API providing required skills

        // Create a map for form parameters to send in the POST request
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        // Add the occupation name parameter from the user's request
        params.add("occupation_name", occupationName);

        // Create HTTP headers for the request
        HttpHeaders headers = new HttpHeaders();
        // Specify that we are sending form-urlencoded data
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Combine headers and parameters into a single HttpEntity object
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // Send POST request to the external API and map the JSON response to an array of RequiredSkillDTO
        RequiredSkillResponse[] response = restTemplate.postForObject(REQUIRED_SKILLS_URL, requestEntity, RequiredSkillResponse[].class);

        // If the API returns nothing, throw an exception
        if (response == null) {
            throw new RuntimeException("No data returned from external API");
        }

        // Normalize citizenSkills σε lowercase για consistency
        List<String> normalizedCitizenSkills = citizenSkills.stream()
                .map(String::toLowerCase)
                .toList();

        // Process the response: filter by minValue, exclude skills the citizen already has (case-insensitive),
        // convert remaining skills to MissingSkillDTO (σε lowercase), and collect into a list
        List<MissingSkillDTO> missingSkills = Arrays.stream(response)
                .filter(skill -> skill.getValue() >= minValue)
                .filter(skill -> !normalizedCitizenSkills.contains(skill.getSkill().toLowerCase()))
                .map(skill -> new MissingSkillDTO(
                        skill.getSkill().toLowerCase(), // normalize skill names
                        skill.getValue(),
                        skill.getPillar(),
                        skill.getSkillId()
                ))
                .toList();

        //  If no missing skills remain, the user already has all required skills
        if (missingSkills.isEmpty()) {
            throw new RuntimeException("You already possess all required skills for this occupation.");
        }

        return missingSkills;
    }

}
