package com.dennis.assessment_service.controller;

import com.dennis.assessment_service.model.lab.LabSubmission;
import com.dennis.assessment_service.service.LabSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lab-submissions")
@RequiredArgsConstructor
@Validated
public class LabSubmissionController {
    private final LabSubmissionService labSubmissionService;


    @Operation(summary = "Submit a lab", description = "Allows a user to submit a lab for a specific assignment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lab submission created successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LabSubmission.class))),
            @ApiResponse(responseCode = "400", description = "Invalid submission data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lab not found", content = @Content)
    })
    @PostMapping("/trainee/{labId}")
    public ResponseEntity<LabSubmission> submitLab(
            @PathVariable @NotNull(message = "Lab id cannot be null") UUID labId, @Valid @RequestBody LabSubmission labSubmission) {
        LabSubmission createdLabSubmission = labSubmissionService.submitLab(labId, labSubmission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabSubmission);
    }

    @Operation(summary = "Get lab submissions", description = "Retrieves all submissions for a specific lab")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lab submissions retrieved successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LabSubmission.class))),
            @ApiResponse(responseCode = "404", description = "Lab not found", content = @Content)
    })
    @GetMapping("/trainer/{labId}")
    public ResponseEntity<List<LabSubmission>> getLabSubmissions(@PathVariable @NotNull(message = "Lab id cannot be null") UUID labId) {
        List<LabSubmission> labSubmissions = labSubmissionService.getSubmissionsByLabId(labId);
        return ResponseEntity.ok(labSubmissions);
    }
}
