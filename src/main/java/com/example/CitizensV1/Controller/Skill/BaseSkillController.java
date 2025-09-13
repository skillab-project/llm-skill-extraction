package com.example.CitizensV1.Controller.Skill;

import com.example.CitizensV1.Service.SkillService;

/**
 * Base controller for Skills-related endpoints.
 * Provides access to the SkillService for derived controllers.
 */
public abstract class BaseSkillController {

    // Service layer for handling skill operations
    protected SkillService skillService;

    // Constructor injecting the SkillService
    protected BaseSkillController(SkillService skillService) {
        this.skillService = skillService;
    }
}
