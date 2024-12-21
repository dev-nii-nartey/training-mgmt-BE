package com.trainingmgt.live_quiz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerScore {
    @Id
    @GeneratedValue
    private UUID id;

    private String userId;

    private int totalScore;
    private double averageSpeed;
    private int totalQuestions;
    private int correctAnswers;
    private double accuracy;

    @ElementCollection
    private List<String> strengths;

    @ElementCollection
    private List<String> improvementAreas;

    @ManyToOne
    @JoinColumn(name = "live_quiz_id", nullable = false)
    private LiveQuiz quiz;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public PlayerScore(LiveQuiz quiz, String userId) {
        this.quiz = quiz;
        this.userId = userId;
        this.totalScore = 0;
        this.averageSpeed = 0;
    }
}