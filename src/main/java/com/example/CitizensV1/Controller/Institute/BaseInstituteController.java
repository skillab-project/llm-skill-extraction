package com.example.CitizensV1.Controller.Institute;

import com.example.CitizensV1.Service.InstitutesService;

/**
 * Abstract base controller for institute-related endpoints.
 * Provides a reference to the InstitutesService for child controllers.
 */
public abstract class BaseInstituteController {

    // Service layer reference for institute operations
    protected InstitutesService institutesService;

    // Constructor injecting the InstitutesService
    protected BaseInstituteController(InstitutesService institutesService) {
        this.institutesService = institutesService;
    }
}
