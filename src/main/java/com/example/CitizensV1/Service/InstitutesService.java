package com.example.CitizensV1.Service;

import com.example.CitizensV1.DTO.Skills.MissingSkillDTO;
import com.example.CitizensV1.DTO.Universities.InstituteRecommendationResponse;
import com.example.CitizensV1.DTO.Universities.RecommendedInstituteDTO;
import com.example.CitizensV1.Model.Citizens.Citizen;
import com.example.CitizensV1.Model.Citizens.CitizenSkill;
import com.example.CitizensV1.Model.Institute;
import com.example.CitizensV1.Model.Occupations.Occupation;
import com.example.CitizensV1.Model.Occupations.OccupationSkill;
import com.example.CitizensV1.Model.Skill;
import com.example.CitizensV1.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * InstitutesService manages operations related to educational institutes.
 * It provides:
 * - CRUD operations for institutes
 * - Matching institutes to citizens based on missing skills
 * - Coverage calculations for upskill and reskill
 */
@Service
public class InstitutesService {

    private final InstituteRepository institutesRepository;
    private final SkillRepository skillRepository;
    private final CitizenSkillRepository citizenSkillRepository;
    private final CitizensRepository citizensRepository;
    private final OccupationsRepository occupationsRepository;
    private SkillService skillService;
    private RestTemplate restTemplate;
    private static final String UNIVERSITIES_BY_SKILLS_URL = "https://portal.skillab-project.eu/curriculum-skills/get_universities_by_skills?";

    public InstitutesService(InstituteRepository institutesRepository, SkillRepository skillRepository,
                             CitizenSkillRepository citizenSkillRepository, CitizensRepository citizensRepository,
                             OccupationsRepository occupationsRepository, SkillService skillService) {
        this.institutesRepository = institutesRepository;
        this.skillRepository = skillRepository;
        this.citizenSkillRepository = citizenSkillRepository;
        this.citizensRepository = citizensRepository;
        this.occupationsRepository = occupationsRepository;
        this.skillService = skillService;
        this.restTemplate = new RestTemplate();
    }

    //-------------------------------INSTITUTES---------------------------------
    // Returns all institutes
    public List<Institute> getInstitutes() {
        return institutesRepository.findAll();
    }

    //-------------------------------RECOMMENDED INSTITUTES-------------------
    // Returns recommended institutes for a citizen based on missing skills
    //ITS IN PYTHON MIGHT DELETE LATER
    public List<Map<String, Object>> getInstitutesForCitizen(Long citizenId) {
        try {
            Citizen citizen = citizensRepository.findById(citizenId)
                    .orElseThrow(() -> new RuntimeException("Citizen not found"));

            String targetOccupationName = citizen.getTargetOccupation();
            Occupation occupation = occupationsRepository.findByName(targetOccupationName)
                    .orElseThrow(() -> new RuntimeException("Target occupation not found"));

            Map<String, Integer> citizenSkills = new HashMap<>();
            List<CitizenSkill> skills = citizenSkillRepository.findByCitizenId(citizenId);
            for (CitizenSkill cs : skills) {
                citizenSkills.put(cs.getSkill().getTitle(), cs.getLevel());
            }

            List<String> upskillSkills = new ArrayList<>();
            List<String> reskillSkills = new ArrayList<>();
            for (OccupationSkill os : occupation.getRequiredSkills()) {
                String skillName = os.getSkill().getTitle();
                int requiredLevel = os.getLevel();
                int citizenLevel = citizenSkills.getOrDefault(skillName, 0);

                if (citizenLevel < requiredLevel) {
                    if (citizenLevel > 0) upskillSkills.add(skillName);
                    else reskillSkills.add(skillName);
                }
            }

            List<Institute> institutes = institutesRepository.findAll();
            List<Map<String, Object>> recommendations = new ArrayList<>();

            for (Institute inst : institutes) {
                List<String> instSkills = inst.getOfferedSkills().stream()
                        .map(Skill::getTitle)
                        .toList();

                List<String> teachableUpskill = upskillSkills.stream()
                        .filter(instSkills::contains)
                        .toList();

                List<String> teachableReskill = reskillSkills.stream()
                        .filter(instSkills::contains)
                        .toList();

                List<String> totalTeachable = new ArrayList<>();
                totalTeachable.addAll(teachableUpskill);
                totalTeachable.addAll(teachableReskill);

                if (!totalTeachable.isEmpty()) {
                    double coverage = Math.round((double) totalTeachable.size() /
                            (upskillSkills.size() + reskillSkills.size()) * 10000.0) / 100.0;

                    Map<String, Object> rec = new HashMap<>();
                    rec.put("id", inst.getId());
                    rec.put("name", inst.getName());
                    rec.put("website", inst.getWebsite());
                    rec.put("address", inst.getAddress());
                    rec.put("country", inst.getCountry());
                    rec.put("region", inst.getRegion());
                    rec.put("providerType", inst.getProviderType());
                    rec.put("upskillSkills", teachableUpskill);
                    rec.put("reskillSkills", teachableReskill);
                    rec.put("coveragePercentage", coverage);

                    recommendations.add(rec);
                }
            }

            recommendations.sort((a, b) -> Double.compare((double) b.get("coveragePercentage"),
                    (double) a.get("coveragePercentage")));

            return recommendations;

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //-------------------------------POST / INSTITUTES-------------------------
    // Adds a new institute, throws exception if name exists
    public void addNewInstitute(Institute institute) {
        Optional<Institute> optionalInstitute = institutesRepository.findByName(institute.getName());
        if (optionalInstitute.isPresent()) {
            throw new IllegalStateException("Institute with name " + institute.getName() + " already exists");
        }
        institutesRepository.save(institute);
    }

    // Adds multiple institutes at once (bulk)
    public void addNewInstitutes(List<Institute> institutes) {
        List<Institute> uniqueInstitutes = institutes.stream()
                .filter(i -> !institutesRepository.findByName(i.getName()).isPresent())
                .toList();

        if (!uniqueInstitutes.isEmpty()) {
            institutesRepository.saveAll(uniqueInstitutes);
        }
    }

    //-------------------------------SKILLS--------------------------------------
    /**
     * Finds and recommends universities/institutes for a given occupation based on missing skills.
     *
     * @param targetOccupation the occupation the user wants to pursue
     * @param userSkills the list of skills the user already has
     * @param minValue the minimum threshold value for required skills to be considered
     * @return InstituteRecommendationResponse containing the requested skills and recommended institutes
     */
    public InstituteRecommendationResponse findRecommendedInstitutes(String targetOccupation, List<String> userSkills, Double minValue) {

        // 1. Find the missing skills for the given occupation
        List<MissingSkillDTO> missingSkills = skillService.findMissingSkills(targetOccupation, userSkills, minValue);

        // Build the list of requested skills (only the names of missing skills)
        List<String> requestedSkills = missingSkills.stream()
                .map(MissingSkillDTO::getSkill)
                .toList();

        // 2. Prepare the GET request for /get_universities_by_skills
        String skillQuery = missingSkills.stream()
                .map(MissingSkillDTO::getSkill)
                .collect(Collectors.joining("&skills=", "skills=", ""));

        String url = UNIVERSITIES_BY_SKILLS_URL + skillQuery;

        // Execute the GET request and map the response
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> bestMatches = (List<Map<String, Object>>) response.get("best_matches");

        if (bestMatches == null || bestMatches.isEmpty()) {
            throw new RuntimeException("No university was found that covers the missing skills");
        }

        List<RecommendedInstituteDTO> recommended = new ArrayList<>();
        double maxWeight = missingSkills.stream().mapToDouble(MissingSkillDTO::getValue).sum();

        for (Map<String, Object> institute : bestMatches) {
            List<String> presentSkills = (List<String>) institute.get("present_skills");
            double totalWeight = missingSkills.stream()
                    .filter(ms -> presentSkills.contains(ms.getSkill()))
                    .mapToDouble(MissingSkillDTO::getValue)
                    .sum();

            double coverage = maxWeight > 0 ? (totalWeight / maxWeight) * 100 : 0;
            coverage = Math.round(coverage * 100.0) / 100.0;

            List<String> missingInstituteSkills = (List<String>) institute.get("missing_skills");
            List<String> offeredSkills = presentSkills;

            recommended.add(new RecommendedInstituteDTO(
                    (String) institute.get("university"),
                    coverage,
                    missingInstituteSkills,
                    offeredSkills
            ));
        }

        recommended.sort((a, b) -> Double.compare(b.getCoverage(), a.getCoverage()));

        return new InstituteRecommendationResponse(requestedSkills, recommended);
    }


    //-------------------------------DELETE / INSTITUTES------------------------
    // Deletes an institute by ID
    public void deleteInstitute(Long id) {
        Optional<Institute> institute = institutesRepository.findById(id);
        if (!institute.isPresent()) {
            throw new IllegalStateException("Institute with id " + id + " does not exist");
        }
        institutesRepository.deleteById(id);
    }

}
