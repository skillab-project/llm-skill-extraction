package com.example.CitizensV1.Controller.Citizen;

import com.example.CitizensV1.Service.CitizensService;

/**
 * Base controller for all citizen-related controllers.
 * Holds a reference to the CitizensService for shared use.
 */
public abstract class BaseCitizenController {

    // Service used to handle citizen-related business logic
    protected CitizensService citizenService;

    // Constructor to inject the CitizensService
    protected BaseCitizenController(CitizensService citizenService) {
        this.citizenService = citizenService;
    }
}
