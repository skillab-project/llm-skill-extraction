package com.example.CitizensV1.Controller.Citizen;

import com.example.CitizensV1.Service.CitizensService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling DELETE operations for citizens.
 * Supports deleting skills and target occupations for a given citizen.
 */
@RestController
@RequestMapping("/api/v1/citizens")
public class CitizenDeleteController extends BaseCitizenController {

    // Constructor injecting the CitizensService
    public CitizenDeleteController(CitizensService citizenService) {
        super(citizenService);
    }

    //------------------------SKILLS---------------------------------------------

    @Operation(
            summary = "Delete a skill from a citizen",
            description = "Removes the specified skill from the citizen's skill list by citizen ID and skill title."
    )
    @DeleteMapping(path = "{citizenId}/citizenSkill")
    public void deleteCitizenSkill(@PathVariable("citizenId") Long citizenId,
                                   @RequestParam("title") String skillTitle) {
        citizenService.deleteSkillFromCitizen(citizenId, skillTitle);
    }

    //---------------------TARGET OCCUPATION-----------------------------------

    @Operation(
            summary = "Delete the target occupation of a citizen",
            description = "Deletes the target occupation for the given citizen ID."
    )
    @DeleteMapping(path = "{citizenId}/targetOccupation")
    public void deleteTargetOccupation(@PathVariable("citizenId") Long citizenId) {
        citizenService.updateTargetOccupation(citizenId, null);
    }

}
