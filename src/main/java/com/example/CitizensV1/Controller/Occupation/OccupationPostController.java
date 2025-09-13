package com.example.CitizensV1.Controller.Occupation;

import com.example.CitizensV1.DTO.Occupations.OccupationRecommendationRequest;
import com.example.CitizensV1.DTO.Occupations.OccupationRecommendationResponse;
import com.example.CitizensV1.Model.Occupations.Occupation;
import com.example.CitizensV1.Service.OccupationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling POST operations for occupations.
 * Supports adding new occupations and recommending occupations based on user's skills.
 */
@RestController
@RequestMapping("/api/v1/occupation")
public class OccupationPostController extends BaseOccupationController {

    // Constructor injecting the OccupationsService
    public OccupationPostController(OccupationsService occupationsService) {
        super(occupationsService);
    }

    //-------------------------------OCCUPATIONS-------------------------------------

    @Operation(
            summary = "Register a new occupation",
            description = "Adds a single new occupation to the system."
    )
    @PostMapping
    public void registerNewOccupation(@RequestBody Occupation occupation) {
        occupationsService.addNewOccupation(occupation);
    }

    @Operation(
            summary = "Register multiple occupations (bulk)",
            description = "Adds multiple occupations at once to the system."
    )
    @PostMapping(path = "bulk")
    public void registerNewOccupations(@RequestBody List<Occupation> occupations) {
        occupationsService.addNewOccupations(occupations);
    }

    //--------------------------RECOMMENDED OCCUPATIONS -------------------------------

    @Operation(
            summary = "Recommend occupations based on user's skills",
            description = "Returns a filtered list of occupations that match the user's skills above the given threshold."
    )
    @PostMapping(path = "recommend_occupations")
    public ResponseEntity<OccupationRecommendationResponse> recommendOccupations(
            @Parameter(description = "Minimum number of skills that should match for an occupation", required = true)
            @RequestParam int matchingNumber,

            @Parameter(description = "Minimum 'Matching' score required to keep an occupation", required = false)
            @RequestParam(defaultValue = "1.0") double threshold,

            @Parameter(description = "User's skill list", required = true)
            @RequestBody OccupationRecommendationRequest request) {

        OccupationRecommendationResponse response =
                occupationsService.recommendOccupations(request.getSkill_list(), matchingNumber, threshold);

        return ResponseEntity.ok(response);
    }
}
