package com.example.CitizensV1.Controller.Citizen;

import com.example.CitizensV1.Service.CitizensService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling PUT (update) operations for citizens.
 * Supports updating skills, current occupation, and target occupation for a given citizen.
 */
@RestController
@RequestMapping("/api/v1/citizens")
public class CitizenPutController extends BaseCitizenController {

    // Constructor injecting the CitizensService
    public CitizenPutController(CitizensService citizenService) {
        super(citizenService);
    }

    //------------------------SKILLS---------------------------------------------

    @Operation(
            summary = "Update a citizen's skill",
            description = "Updates the given skill and level for a citizen identified by citizenId."
    )
    @PutMapping(path = "{citizenId}/citizenSkills")
    public void updateCitizenSkills(@PathVariable("citizenId") Long citizenId,
                                    @RequestParam(required = true) String citizenSkill,
                                    @RequestParam(required = true) int level) {
        citizenService.updateSkills(citizenId, citizenSkill, level);
    }

    //---------------------CURRENT OCCUPATION-----------------------------------

    @Operation(
            summary = "Update the current occupation of a citizen",
            description = "Updates the current occupation for the citizen identified by citizenId."
    )
    @PutMapping(path = "{citizenId}/currentOccupation")
    public void updateCurrentOccupation(@PathVariable("citizenId") Long citizenId,
                                        @RequestParam String currentOccupation) {
        citizenService.updateCurrentOccupation(citizenId, currentOccupation);
    }

    //---------------------TARGET OCCUPATION-----------------------------------

    @Operation(
            summary = "Update the target occupation of a citizen",
            description = "Updates the target occupation for the citizen identified by citizenId."
    )
    @PutMapping(path = "{citizenId}/targetOccupation")
    public void updateTargetOccupation(@PathVariable("citizenId") Long citizenId,
                                       @RequestParam String targetOccupation) {
        citizenService.updateTargetOccupation(citizenId, targetOccupation);
    }

}
