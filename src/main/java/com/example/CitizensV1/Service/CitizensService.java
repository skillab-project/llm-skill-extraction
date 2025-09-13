package com.example.CitizensV1.Service;

import com.example.CitizensV1.Model.Citizens.Citizen;
import com.example.CitizensV1.Model.Citizens.CitizenSkill;
import com.example.CitizensV1.Model.Skill;
import com.example.CitizensV1.Repository.CitizenSkillRepository;
import com.example.CitizensV1.Repository.CitizensRepository;
import com.example.CitizensV1.Repository.SkillRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * CitizensService manages all operations related to citizens.
 * It includes:
 * - CRUD operations for citizens
 * - Skill management
 * - Managing current and target occupations
 */
@Service
public class CitizensService {

    private final CitizensRepository citizenRepository;
    private final SkillRepository skillRepository;
    private final CitizenSkillRepository citizenSkillRepository;

    public CitizensService(CitizensRepository citizenRepository, SkillRepository skillRepository, CitizenSkillRepository citizenSkillRepository) {
        this.citizenRepository = citizenRepository;
        this.skillRepository = skillRepository;
        this.citizenSkillRepository = citizenSkillRepository;
    }

    //-----------------------GET METHODS-------------------------------------

    // Returns all citizens
    public List<Citizen> getCitizens() {
        return citizenRepository.findAll();
    }

    // Returns a map of skillName -> level for a citizen
    public Map<String, Integer> getCitizenSkills(Long citizenId) {
        List<CitizenSkill> skills = citizenSkillRepository.findByCitizenId(citizenId);
        Map<String, Integer> result = new HashMap<>();
        for (CitizenSkill cs : skills) {
            result.put(cs.getSkill().getTitle(), cs.getLevel());
        }
        return result;
    }

    // Returns the target occupation of a citizen
    public ResponseEntity<Map<String, Object>> getTargetOccupation(Long citizenId) {
        return citizenRepository.findById(citizenId)
                .map(citizen -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("citizenId", citizenId);
                    response.put("targetOccupation", citizen.getTargetOccupation());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //-----------------------POST / ADD METHODS--------------------------------

    // Adds a new citizen, throws exception if email exists
    @Transactional
    public void addNewCitizen(Citizen citizen) {
        Optional<Citizen> optionalCitizen = citizenRepository.findByEmail(citizen.getEmail());
        if(optionalCitizen.isPresent()) {
            throw new IllegalArgumentException("Citizen with email " + citizen.getEmail() + " already exists");
        }
        citizenRepository.save(citizen);
    }

    // Adds multiple citizens at once (bulk)
    @Transactional
    public void addBulkCitizens(List<Citizen> citizens) {
        for (Citizen citizen : citizens) {
            Optional<Citizen> optionalCitizen = citizenRepository.findByEmail(citizen.getEmail());
            if(optionalCitizen.isPresent()) {
                throw new IllegalArgumentException("Citizen with email " + citizen.getEmail() + " already exists");
            }
        }
        citizenRepository.saveAll(citizens);
    }

    //-----------------------SKILL METHODS-----------------------------------

    // Adds or updates a skill for a citizen
    @Transactional
    public void updateSkills(Long id, String skillName, int level) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citizen with id " + id + " not found"));

        if (skillName != null && !skillName.isEmpty() && level >= 0 && level <= 5) {
            List<CitizenSkill> skillsList = citizen.getSkills();
            boolean found = false;

            // Check if citizen already has the skill
            for (CitizenSkill sl : skillsList) {
                if (sl.getSkill().getTitle().equalsIgnoreCase(skillName)) {
                    sl.setLevel(level);
                    found = true;
                    break;
                }
            }

            // If skill not found, create new
            if (!found) {
                Skill skill = skillRepository.findByTitle(skillName)
                        .orElseGet(() -> {
                            Skill s = new Skill();
                            s.setTitle(skillName);
                            return skillRepository.save(s);
                        });

                CitizenSkill newSkillLevel = new CitizenSkill();
                newSkillLevel.setSkill(skill);
                newSkillLevel.setLevel(level);
                newSkillLevel.setCitizen(citizen);

                skillsList.add(newSkillLevel);
            }

            citizen.setSkills(skillsList);
        }
    }

    // Deletes a specific skill from a citizen
    @Transactional
    public void deleteSkillFromCitizen(Long citizenId, String skillTitle) {
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen with id " + citizenId + " not found"));

        citizen.getSkills().removeIf(cs -> cs.getSkill().getTitle().equalsIgnoreCase(skillTitle));
    }

    //-----------------------CURRENT OCCUPATION METHODS-----------------------

    // Updates the current occupation for a citizen
    @Transactional
    public void updateCurrentOccupation(Long id, String currentOccupation) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citizen with id " + id + " not found"));
        citizen.setCurrentOccupation(currentOccupation);
    }

    // Deletes the current occupation of a citizen
    @Transactional
    public void deleteCurrentOccupation(Long id) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citizen with id " + id + " not found"));
        citizen.setCurrentOccupation(null);
    }

    //-----------------------TARGET OCCUPATION METHODS------------------------

    // Updates the target occupation for a citizen
    @Transactional
    public void updateTargetOccupation(Long id, String targetOccupation) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citizen with id " + id + " not found"));

        if (targetOccupation == null) {
            citizen.setTargetOccupation(null);
        } else if (!targetOccupation.isBlank()) {
            citizen.setTargetOccupation(targetOccupation);
        }
    }

}
