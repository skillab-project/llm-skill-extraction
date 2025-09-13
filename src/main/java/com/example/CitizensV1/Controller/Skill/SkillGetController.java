package com.example.CitizensV1.Controller.Skill;

import com.example.CitizensV1.Model.Skill;
import com.example.CitizensV1.Service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for handling GET operations for skills.
 * Supports retrieving all skills in the system.
 */
@RestController
@RequestMapping("/api/v1/skills")
public class SkillGetController extends BaseSkillController {

    // Constructor injecting the SkillService
    public SkillGetController(SkillService skillService){
        super(skillService);
    }

    //-------------------------------SKILLS-------------------------------------

    @Operation(
            summary = "Get all skills",
            description = "Retrieves a list of all registered skills from the system."
    )
    @GetMapping
    public List<Skill> getSkills() {
        return skillService.getSkills();
    }
}
