package com.trainingmgt.live_quiz.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.trainingmgt.live_quiz.enums.QuestionStatus;
import com.trainingmgt.live_quiz.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue
    private UUID id;

    private String questionText;

    @ElementCollection
    private List<String> options;

    private String correctAnswer;

    private long duration;

    private int points;

    private String explanation;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    private QuestionStatus status;

    @ManyToOne
    @JoinColumn(name = "live_quiz_id", nullable = false)
    @JsonBackReference
    private LiveQuiz liveQuiz;

    @ElementCollection
    @CollectionTable(name = "question_tags", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "tag")
    private List<String> tags;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
