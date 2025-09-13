package com.example.CitizensV1.Controller.Occupation;

import com.example.CitizensV1.Service.OccupationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling DELETE operations for occupations.
 * Supports deleting a single occupation by ID.
 */
@RestController
@RequestMapping("/api/v1/occupation")
public class OccupationDeleteController extends BaseOccupationController {

    // Constructor injecting the OccupationsService
    public OccupationDeleteController(OccupationsService occupationsService) {
        super(occupationsService);
    }

    //-------------------------------OCCUPATION-------------------------------------

    @Operation(
            summary = "Delete an occupation",
            description = "Deletes a specific occupation from the system using its ID."
    )
    @DeleteMapping(path = "{occupationId}")
    public void deleteOccupation(
            @Parameter(description = "ID of the occupation to delete", required = true)
            @PathVariable("occupationId") Long occupationId) {
        occupationsService.deleteOccupation(occupationId);
    }
}
