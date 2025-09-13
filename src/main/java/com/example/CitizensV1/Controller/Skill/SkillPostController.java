package com.example.CitizensV1.Controller.Skill;

import com.example.CitizensV1.DTO.Skills.MissingSkillDTO;
import com.example.CitizensV1.Model.Skill;
import com.example.CitizensV1.Service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling POST operations for skills.
 * Supports registering new skills and finding missing skills for a citizen.
 */
@RestController
@RequestMapping("/api/v1/skills")
public class SkillPostController extends BaseSkillController {

    // Constructor injecting the SkillService
    public SkillPostController(SkillService skillService) {
        super(skillService);
    }

    //-------------------------------SKILLS-------------------------------------

    @Operation(
            summary = "Register a new skill",
            description = "Adds a new skill to the system using the provided Skill object."
    )
    @PostMapping
    public void registerNewSkill(@RequestBody Skill skill) {
        skillService.addNewSkill(skill);
    }

    @Operation(
            summary = "Register multiple new skills",
            description = "Adds a list of new skills to the system in bulk."
    )
    @PostMapping(path = "bulk")
    public void registerNewSkills(@RequestBody List<Skill> skills) {
        skillService.addNewSkills(skills);
    }

    //------------------------MISSING SKILLS-------------------------------------

    @Operation(
            summary = "Get missing skills for a citizen",
            description = "Returns a list of missing skills for a given citizen based on their current skills "
                    + "and the required skills for a target occupation."
    )
    @PostMapping(path = "find_missing_skills")
    public List<MissingSkillDTO> findCitizensMissingSkills(
            @Parameter(description = "Name of the occupation / role", required = true)
            @RequestParam String occupationName,

            @Parameter(description = "List of skills the citizen already has", required = true)
            @RequestParam List<String> citizenSkills,

            @Parameter(description = "Minimum skill value to consider (default 0.5)")
            @RequestParam(defaultValue = "0.5") Double minValue
    ) {
        return skillService.findMissingSkills(occupationName, citizenSkills, minValue);
    }
}
