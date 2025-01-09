package com.trainingmgt.live_quiz.resource;

import java.util.List;
import java.util.UUID;

public class QuizReport{
    private String playerId;
    private UUID quizId;
    private int score;
    private int correctAnswers;
    private int totalQuestions;
    private double accuracy;
    private List<String> strengths;
    private List<String> improvementAreas;

    public QuizReport(String playerId, UUID quizId, int score, int correctAnswers, int totalQuestions, double accuracy,
                         List<String> strengths, List<String> improvementAreas) {
        this.playerId = playerId;
        this.quizId = quizId;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.accuracy = accuracy;
        this.strengths = strengths;
        this.improvementAreas = improvementAreas;
    }

    // Getters and Setters
    // ...
}
