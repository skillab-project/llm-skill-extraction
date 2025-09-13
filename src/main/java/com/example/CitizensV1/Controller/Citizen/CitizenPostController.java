package com.example.CitizensV1.Controller.Citizen;

import com.example.CitizensV1.Model.Citizens.Citizen;
import com.example.CitizensV1.Service.CitizensService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling POST operations for citizens.
 * Supports registering new citizens, both single and bulk registration.
 */
@RestController
@RequestMapping("/api/v1/citizens")
public class CitizenPostController extends BaseCitizenController {

    // Constructor injecting the CitizensService
    public CitizenPostController(CitizensService citizenService) {
        super(citizenService);
    }

    //-------------------------------CITIZEN(S)-------------------------------------

    @Operation(
            summary = "Register a new citizen",
            description = "Registers a single new citizen using the JSON request body mapped to a Citizen object."
    )
    @PostMapping
    public void registerNewCitizen(@RequestBody Citizen citizen) {
        citizenService.addNewCitizen(citizen);
    }

    @Operation(
            summary = "Register multiple citizens in bulk",
            description = "Registers a list of citizens at once using the JSON request body mapped to a list of Citizen objects."
    )
    @PostMapping(path = "bulk")
    public void registerBulkCitizens(@RequestBody List<Citizen> citizens) {
        citizenService.addBulkCitizens(citizens);
    }
}
