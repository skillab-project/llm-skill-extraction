package com.example.CitizensV1.Controller.Institute;

import com.example.CitizensV1.DTO.Universities.InstituteRecommendationResponse;
import com.example.CitizensV1.Model.Institute;
import com.example.CitizensV1.Service.InstitutesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling POST operations for institutes/universities.
 * Supports registering new institutes (single and bulk) and recommending institutes based on user skills.
 */
@RestController
@RequestMapping("/api/v1/institutes")
public class InstitutePostController extends BaseInstituteController {

    // Constructor injecting InstitutesService
    public InstitutePostController(InstitutesService institutesService) {
        super(institutesService);
    }

    //-------------------------------INSTITUTES-------------------------------------

    @Operation(
            summary = "Register a new institute",
            description = "Adds a new institute/university to the system using the provided JSON request body."
    )
    @PostMapping
    public void registerNewInstitute(@RequestBody Institute institute) {
        institutesService.addNewInstitute(institute);
    }

    @Operation(
            summary = "Register multiple new institutes",
            description = "Adds a list of institutes/universities in bulk using the provided JSON request body."
    )
    @PostMapping(path = "bulk")
    public void registerNewInstitutes(@RequestBody List<Institute> institutes) {
        institutesService.addNewInstitutes(institutes);
    }

    //------------------------RECOMMEND UNIVERSITIES--------------------------------

    @Operation(
            summary = "Get recommended universities for target occupation based on skills",
            description = "Returns recommended universities that can help the user acquire missing skills for a target occupation."
    )
    @PostMapping(path = "recommend_universities")
    public InstituteRecommendationResponse recommendUniversities(
            @Parameter(description = "Target occupation", required = true)
            @RequestParam String targetOccupation,

            @Parameter(description = "List of user's skills", required = true)
            @RequestParam List<String> skills,

            @Parameter(description = "Minimum skill value for consideration", required = false)
            @RequestParam(defaultValue = "0.5") Double minValue
    ) {
        return institutesService.findRecommendedInstitutes(targetOccupation, skills, minValue);
    }
}
