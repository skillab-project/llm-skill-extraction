package com.example.CitizensV1.Controller.Institute;

import com.example.CitizensV1.Service.InstitutesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling DELETE operations for institutes/universities.
 * Supports deleting a single institute by its ID.
 */
@RestController
@RequestMapping("/api/v1/institutes")
public class

InstituteDeleteController extends BaseInstituteController {


    // Constructor injecting InstitutesService
    public InstituteDeleteController(InstitutesService institutesService) {
        super(institutesService);
    }

    //------------------------DELETE INSTITUTE-------------------------------------

    @Operation(
            summary = "Delete an institute",
            description = "Deletes a specific institute/university from the system by its ID."
    )
    @DeleteMapping(path = "{instituteId}")
    public void deleteInstitute(
            @Parameter(description = "ID of the institute to delete", required = true)
            @PathVariable("instituteId") Long instituteId
    ) {
        institutesService.deleteInstitute(instituteId);
    }
}
