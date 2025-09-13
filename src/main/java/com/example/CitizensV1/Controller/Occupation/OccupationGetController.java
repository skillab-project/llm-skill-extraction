package com.example.CitizensV1.Controller.Occupation;

import com.example.CitizensV1.Model.Occupations.Occupation;
import com.example.CitizensV1.Service.OccupationsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling GET operations for occupations.
 * Supports retrieving all occupations, specific occupation details,
 * and recommended occupations for a citizen.
 */
@RestController
@RequestMapping("/api/v1/occupation")
public class OccupationGetController extends BaseOccupationController {

    // Constructor injecting the OccupationsService
    public OccupationGetController(OccupationsService occupationsService) {
        super(occupationsService);
    }

    //-------------------------------OCCUPATIONS-------------------------------------

    @Operation(
            summary = "Get all occupations",
            description = "Retrieves a list of all occupations available in the system."
    )
    @GetMapping
    public List<Occupation> getOccupations() {
        return occupationsService.getOccupations();
    }

    @Operation(
            summary = "Get occupation by name",
            description = "Retrieves detailed information for a specific occupation by its name."
    )
    @GetMapping(path = "{occupationName}")
    public ResponseEntity<?> getOccupationByName(@PathVariable String occupationName) {
        return occupationsService.getOccupationDetails(occupationName);
    }

    //---------------------------- RECOMMENDED OCCUPATIONS ---------------------------

    //ITS FOR PYTHON MIGHT DELETE LATER
    @Operation(
            summary = "Get recommended occupations for a citizen",
            description = "Returns a list of occupations recommended for a specific citizen based on their skills and target occupation."
    )
    @GetMapping(path = "{citizenId}/recommend_occupations_PYTHON")
    public ResponseEntity<?> getRecommendedOccupations(@PathVariable Long citizenId) {
        return ResponseEntity.ok(occupationsService.getRecommendedOccupations(citizenId));
    }
}
