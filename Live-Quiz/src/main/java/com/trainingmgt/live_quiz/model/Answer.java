package com.trainingmgt.live_quiz.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String userId;
    private String selectedAnswer;

    private LocalDateTime submittedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "live_quiz_id", nullable = false)
    private LiveQuiz quiz;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public boolean isCorrect() {
        return selectedAnswer.equalsIgnoreCase(question.getCorrectAnswer());
    }
}