package com.example.CitizensV1.Controller.Occupation;

import com.example.CitizensV1.Service.OccupationsService;

/**
 * Base controller for occupation-related endpoints.
 * Provides access to the OccupationsService for derived controllers.
 */
public abstract class BaseOccupationController {

    // Service layer handling occupation logic
    protected OccupationsService occupationsService;

    // Constructor injecting the OccupationsService
    protected BaseOccupationController(OccupationsService occupationsService) {
        this.occupationsService = occupationsService;
    }
}
