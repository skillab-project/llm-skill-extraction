package com.example.CitizensV1.Controller.Institute;

import com.example.CitizensV1.Model.Institute;
import com.example.CitizensV1.Service.InstitutesService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller for handling GET operations for institutes/universities.
 * Supports retrieving all institutes and recommended institutes for a citizen.
 */
@RestController
@RequestMapping("/api/v1/institutes")
public class InstituteGetController extends BaseInstituteController {

    // Constructor injecting the InstitutesService
    public InstituteGetController(InstitutesService institutesService) {
        super(institutesService);
    }

    //-------------------------------INSTITUTES-------------------------------------

    @Operation(
            summary = "Get all institutes",
            description = "Retrieves a list of all registered institutes/universities."
    )
    @GetMapping
    public List<Institute> getInstitutes() {
        return institutesService.getInstitutes();
    }

    //------------------------RECOMMENDED INSTITUTES FOR CITIZEN--------------------

    //ITS FOR PYTHON MIGHT DELETE LATER
    @Operation(
            summary = "Get recommended institutes for a citizen",
            description = "Returns a list of recommended institutes for a specific citizen, "
                    + "including details like coverage and missing/offered skills."
    )
    @GetMapping(path ="RecommendedforCitizen_PYTHON/{citizenId}")
    public List<Map<String, Object>> getInstitutesForCitizen(@PathVariable Long citizenId) {
        return institutesService.getInstitutesForCitizen(citizenId);
    }
}
