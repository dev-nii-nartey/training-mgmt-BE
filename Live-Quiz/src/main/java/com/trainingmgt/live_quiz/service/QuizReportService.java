package com.trainingmgt.live_quiz.service;

import com.trainingmgt.live_quiz.model.Answer;
import com.trainingmgt.live_quiz.model.PlayerScore;
import com.trainingmgt.live_quiz.repository.AnswerRepository;
import com.trainingmgt.live_quiz.repository.PlayerScoreRepository;
import com.trainingmgt.live_quiz.resource.QuizReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuizReportService {

    private final PlayerScoreRepository playerScoreRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public QuizReportService(PlayerScoreRepository playerScoreRepository,
                             AnswerRepository answerRepository) {
        this.playerScoreRepository = playerScoreRepository;
        this.answerRepository = answerRepository;
    }

    public QuizReport generateQuizReport(String playerId, UUID quizId) {
        // Fetch the player score
        PlayerScore playerScore = playerScoreRepository.findByUserIdAndQuizId(playerId, quizId)
                .orElseThrow(() -> new RuntimeException("PlayerScore not found for playerId: " + playerId + ", quizId: " + quizId));

        // Fetch the answers for the player and quiz
        List<Answer> answers = answerRepository.findByUserIdAndQuizId(playerId, quizId);

        // Calculate details for the report
        int correctAnswers = (int) answers.stream().filter(Answer::isCorrect).count();
        int totalQuestions = answers.size();
        double accuracy = (double) correctAnswers / totalQuestions * 100;

        // Map strengths and improvement areas
        List<String> strengths = new ArrayList<>();
        List<String> improvementAreas = new ArrayList<>();
        for (Answer answer : answers) {
            if (answer.isCorrect()) {
                strengths.add(answer.getQuestion().getQuestionText());
            } else {
                improvementAreas.add(answer.getQuestion().getQuestionText());
            }
        }

        // Create and return the QuizReportDTO
        return new QuizReport(
                playerScore.getUserId(),
                quizId,
                playerScore.getTotalScore(),
                correctAnswers,
                totalQuestions,
                accuracy,
                strengths,
                improvementAreas
        );
    }
}
