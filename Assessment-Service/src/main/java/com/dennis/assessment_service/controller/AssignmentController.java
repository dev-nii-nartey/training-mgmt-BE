package com.dennis.assessment_service.controller;

import com.dennis.assessment_service.dto.AssessmentDTO;
import com.dennis.assessment_service.dto.AssignBatchAssessmentDTO;
import com.dennis.assessment_service.model.Assignment;
import com.dennis.assessment_service.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assignments")
@Validated
public class AssignmentController {
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Operation(summary = "Assign an assessment to multiple trainees", description = "Assigns an assessment to a batch of trainees")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Assessment assigned to multiple trainees successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/batch")
    public ResponseEntity<String> assignAssessmentToMultipleTrainees(@Valid @RequestBody AssignBatchAssessmentDTO assignBatchAssessmentDTO) {
        assignmentService.assignAssessmentToMultipleTrainees(
                assignBatchAssessmentDTO.getTraineeEmail(),
                assignBatchAssessmentDTO.getAssessmentId(),
                assignBatchAssessmentDTO.getDeadline());
        return ResponseEntity.status(HttpStatus.CREATED).body("Assessment assigned to trainees successfully");
    }


    @Operation(summary = "Assign an assessment to cohort", description = "Assigns an assessment to trainees in a cohort")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Assessment assigned to multiple trainees successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/cohort")
    public ResponseEntity<String> assignAssessmentToCohort(
            @RequestParam @NotNull(message = "Cohort id cannot be null") Long cohortId,
            @RequestParam @NotNull(message = "Assessment id cannot be null") UUID assessmentId,
            @RequestParam @FutureOrPresent(message = "Deadline must be in the future or present") LocalDateTime deadline
    ) {
       assignmentService.assignAssessmentToCohort(cohortId, assessmentId, deadline);
        return ResponseEntity.status(HttpStatus.CREATED).body("Assessment assigned to trainees successfully");
    }

    @Operation(summary = "Get assignments by trainee", description = "Retrieves all assignments for a specific trainee by their email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assignments retrieved successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Assignment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email format", content = @Content)
    })
    @PreAuthorize("hasRole('TRAINEE')")
    @GetMapping("/trainee")
    public ResponseEntity<List<AssessmentDTO>> getAssignmentsByTrainee(@RequestParam @Email(message = "Invalid email format") String traineeEmail) {
        List<AssessmentDTO> assessmentDTOS = assignmentService.getAssignmentsByTrainee(traineeEmail);
        return ResponseEntity.ok(assessmentDTOS);
    }

    @Operation(summary = "Get assignments by assessment", description = "Retrieves all assignments for a specific assessment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assignments retrieved successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Assignment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/assessment/{assessmentId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByAssessment(@PathVariable @NotNull(message = "Assessment ID must not be null") UUID assessmentId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByAssessment(assessmentId);
        return ResponseEntity.ok(assignments);
    }


}
