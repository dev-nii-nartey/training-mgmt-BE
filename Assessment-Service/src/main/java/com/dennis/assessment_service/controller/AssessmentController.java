package com.dennis.assessment_service.controller;

import com.dennis.assessment_service.model.Presentation;
import com.dennis.assessment_service.model.lab.Lab;
import com.dennis.assessment_service.model.quiz.Quiz;
import com.dennis.assessment_service.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
@Validated
public class AssessmentController {
    private final AssessmentService assessmentService;

    @Operation(
            summary = "Create a new quiz",
            description = "Endpoint for trainers to create a new quiz assessment.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Quiz created successfully",
                            content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping(value ="/quiz", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> createQuiz(
            @RequestParam("title") @NotNull(message = "Assessment should have a title") String title,
            @RequestParam("description") @NotBlank(message = "Description is required") String description,
            @RequestParam("focusArea") @NotBlank(message = "Focus area is required") String focusArea,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage
     ) {

       Quiz createdQuiz = assessmentService.createQuiz(title, description, focusArea, coverImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz.getId());
    }

    @Operation(
            summary = "Create a new lab",
            description = "Endpoint for trainers to create a new lab assessment.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Lab created successfully",
                            content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping(value ="/lab",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> createLab(
            @RequestParam("title") @NotBlank(message = "Assessment should have a title") String title,
            @RequestParam("description") @NotBlank(message = "Description is required") String description,
            @RequestParam("focusArea") @NotBlank(message = "Focus area is required") String focusArea,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Lab createdLab = assessmentService.createLab(title, description, focusArea, coverImage, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLab.getId());
    }


    @Operation(
            summary = "Create a new presentation",
            description = "Endpoint for trainers to create a new presentation assessment.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Presentation created successfully",
                            content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping(value ="/presentation",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> createPresentation(
            @RequestParam("title") @NotBlank(message = "Assessment should have a title") String title,
            @RequestParam("description") @NotBlank(message = "Description is required") String description,
            @RequestParam("focusArea") @NotBlank(message = "Focus area is required") String focusArea,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Presentation createdPresentation = assessmentService.createPresentation(title, description, focusArea, coverImage, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPresentation.getId());
    }

    @Operation(
            summary = "Get all assessments",
            description = "Retrieve all quizzes, labs, and presentations.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Assessments retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/all")
    public ResponseEntity<Map<String, List<?>>> getAllAssessments() {
        Map<String, List<?>> assessments = new HashMap<>();
        assessments.put("quizzes", assessmentService.getAllQuizzes());
        assessments.put("labs", assessmentService.getAllLabs());
        assessments.put("presentations", assessmentService.getAllPresentations());
        return ResponseEntity.ok(assessments);
    }
}
