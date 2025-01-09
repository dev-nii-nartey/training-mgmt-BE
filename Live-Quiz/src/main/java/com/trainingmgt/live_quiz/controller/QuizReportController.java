package com.trainingmgt.live_quiz.controller;

import com.trainingmgt.live_quiz.model.PlayerScore;
import com.trainingmgt.live_quiz.repository.PlayerScoreRepository;
import com.trainingmgt.live_quiz.resource.QuizReport;
import com.trainingmgt.live_quiz.service.QuizReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/quiz-reports")
public class QuizReportController {

    private final QuizReportService quizReportService;
    private final PlayerScoreRepository playerScoreRepository;

    @Autowired
    public QuizReportController(QuizReportService quizReportService, PlayerScoreRepository playerScoreRepository) {
        this.quizReportService = quizReportService;
        this.playerScoreRepository = playerScoreRepository;
    }

    /**
     * Get a personalized quiz report for a specific player and quiz.
     *
     * @param playerId The ID of the player.
     * @param quizId   The ID of the quiz.
     * @return QuizReportDTO containing performance details.
     */
    @GetMapping("/{playerId}/quiz/{quizId}")
    public ResponseEntity<QuizReport> getQuizReportForPlayer(
            @PathVariable String playerId,
            @PathVariable UUID quizId) {
        QuizReport report = quizReportService.generateQuizReport(playerId, quizId);
        return ResponseEntity.ok(report);
    }

    /**
     * Get all quiz reports for a specific quiz with optional filters.
     *
     * @param quizId        The ID of the quiz.
     * @param minScore      (Optional) Minimum score filter.
     * @param maxScore      (Optional) Maximum score filter.
     * @param minAccuracy   (Optional) Minimum accuracy filter.
     * @return List of QuizReportDTOs for all players in the quiz.
     */
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizReport>> getQuizReportsForQuiz(
            @PathVariable UUID quizId,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore,
            @RequestParam(required = false) Double minAccuracy) {

        // Fetch all PlayerScores for the quiz
        List<PlayerScore> playerScores = playerScoreRepository.findByQuizId(quizId);

        // Filter player scores based on query parameters
        Stream<PlayerScore> filteredScores = playerScores.stream();

        if (minScore != null) {
            filteredScores = filteredScores.filter(score -> score.getTotalScore() >= minScore);
        }

        if (maxScore != null) {
            filteredScores = filteredScores.filter(score -> score.getTotalScore() <= maxScore);
        }

        if (minAccuracy != null) {
            filteredScores = filteredScores.filter(score -> {
                int correctAnswers = score.getCorrectAnswers();
                int totalQuestions = score.getTotalQuestions();
                double accuracy = (double) correctAnswers / totalQuestions * 100;
                return accuracy >= minAccuracy;
            });
        }

        // Generate detailed reports for all filtered scores
        List<QuizReport> reports = filteredScores.map(playerScore ->
                        quizReportService.generateQuizReport(playerScore.getUserId(), quizId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(reports);
    }
}
