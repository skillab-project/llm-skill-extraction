package com.example.CitizensV1.Controller.Citizen;

import com.example.CitizensV1.Model.Citizens.Citizen;
import com.example.CitizensV1.Service.CitizensService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for handling GET operations for citizens.
 * Supports retrieving citizen information, skills, and target occupations.
 */
@RestController
@RequestMapping("/api/v1/citizens")
public class CitizenGetController extends BaseCitizenController {

    // Constructor injecting the CitizensService
    public CitizenGetController(CitizensService citizenService) {
        super(citizenService);
    }

    //-------------------------------CITIZEN(S)-------------------------------------

    @Operation(
            summary = "Get all citizens",
            description = "Retrieves a list of all citizens from the system."
    )
    @GetMapping
    public List<Citizen> getCitizens() {
        return citizenService.getCitizens();
    }

    //------------------------SKILLS---------------------------------------------

    @Operation(
            summary = "Get skills of a citizen",
            description = "Returns a map containing the citizen ID and their skills (title and level)."
    )
    @GetMapping(path = "{citizenId}/citizenSkills")
    public Map<String, Integer> getCitizenSkills(@PathVariable Long citizenId) {
        return citizenService.getCitizenSkills(citizenId);
    }

    //---------------------TARGET OCCUPATION-----------------------------------

    @Operation(
            summary = "Get the target occupation of a citizen",
            description = "Returns the ID of the citizen and their target occupation."
    )
    @GetMapping(path = "{citizenId}/targetOccupation")
    public ResponseEntity<Map<String, Object>> getTargetOccupation(@PathVariable Long citizenId) {
        return citizenService.getTargetOccupation(citizenId);
    }
}
