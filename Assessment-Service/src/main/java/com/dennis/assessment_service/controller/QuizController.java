package com.dennis.assessment_service.controller;

import com.dennis.assessment_service.dto.quiz.QuestionDTO;
import com.dennis.assessment_service.dto.quiz.QuizSubmissionDTO;
import com.dennis.assessment_service.model.quiz.Question;
import com.dennis.assessment_service.service.quiz.QuizService;
import com.dennis.assessment_service.service.quiz.QuizSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quizzes")
@Validated
public class QuizController {


    private final QuizService quizService;
    private final QuizSubmissionService quizSubmissionService;

    public QuizController(QuizService quizService, QuizSubmissionService quizSubmissionService) {
        this.quizService = quizService;
        this.quizSubmissionService = quizSubmissionService;
    }

    @Operation(summary = "Add multiple questions to a quiz", description = "Adds multiple questions to a quiz by quiz ID with a specified duration")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Batch of questions successfully added"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Quiz not found", content = @Content)
    })
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/trainer/{quizId}/questions/batch")
    public ResponseEntity<String> addQuestionsToQuiz(
            @PathVariable @NotNull(message = "Quiz ID must not be null") UUID quizId,
            @Valid @RequestBody List<Question> questions,
            @RequestParam @Min(value = 1, message = "Quiz duration must be at least 1 minute") Integer quizDuration) {
        quizService.addQuestionsToQuiz(quizId, questions, quizDuration);
        return ResponseEntity.status(HttpStatus.CREATED).body("Batch questions added");
    }


    @Operation(summary = "Get quiz submissions", description = "Retrieves all submissions for a specific quiz by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submissions successfully retrieved", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = QuizSubmissionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Quiz not found", content = @Content)
    })
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/trainer/{quizId}")
    public ResponseEntity<List<QuizSubmissionDTO>> getQuizSubmissions(@PathVariable @NotNull(message = "quiz id must not be null") UUID quizId) {
        List<QuizSubmissionDTO> submissions = quizSubmissionService.getSubmissionsByQuizId(quizId);
        return ResponseEntity.ok(submissions);
    }

    @Operation(summary = "Get all questions with answers for a quiz", description = "Fetches all questions and their answers for a specific quiz by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questions and answers retrieved successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "404", description = "Quiz not found", content = @Content)
    })
    @GetMapping("/all/{quizId}/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByQuizId(
            @PathVariable @NotNull(message = "Quiz ID must not be null") UUID quizId) {
        List<QuestionDTO> questionsDTO = quizService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questionsDTO);
    }

}
