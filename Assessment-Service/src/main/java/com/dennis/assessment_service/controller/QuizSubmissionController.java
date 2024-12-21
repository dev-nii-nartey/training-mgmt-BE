package com.dennis.assessment_service.controller;

import com.dennis.assessment_service.dto.quiz.QuizSubmissionDTO;
import com.dennis.assessment_service.model.quiz.QuizSubmission;
import com.dennis.assessment_service.service.quiz.QuizSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quiz-submissions")
@Validated
public class QuizSubmissionController {
    private final QuizSubmissionService quizSubmissionService;

    public QuizSubmissionController(QuizSubmissionService quizSubmissionService) {
        this.quizSubmissionService = quizSubmissionService;
    }

    @Operation(summary = "Submit a quiz", description = "Allows a trainee to submit answers for a specific quiz")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Quiz submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid submission data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Quiz not found", content = @Content)
    })
    @PreAuthorize("hasRole('TRAINEE')")
    @PostMapping("/trainee/{quizId}")
    public ResponseEntity<String> submitQuiz(
            @PathVariable @NotNull(message = "quiz id must not be null") UUID quizId,
            @Valid @RequestBody QuizSubmission submission) {
        String message = quizSubmissionService.submitQuiz(quizId, submission);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }


    @Operation(summary = "Get quiz submissions", description = "Retrieves all submissions for a specific quiz")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submissions successfully retrieved", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = QuizSubmissionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Quiz not found", content = @Content)
    })


    @GetMapping("/all/{quizId}")
    public ResponseEntity<List<QuizSubmissionDTO>> getQuizSubmissions(@PathVariable @NotNull(message = "quiz id must not be null") UUID quizId) {
        List<QuizSubmissionDTO> submissions = quizSubmissionService.getSubmissionsByQuizId(quizId);
        return ResponseEntity.ok(submissions);
    }
}
