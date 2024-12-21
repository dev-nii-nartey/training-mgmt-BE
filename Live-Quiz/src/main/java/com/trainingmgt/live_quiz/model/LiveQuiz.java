package com.trainingmgt.live_quiz.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.trainingmgt.live_quiz.enums.LiveQuizStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "live_quiz")
public class LiveQuiz {
        @Id
        @GeneratedValue
        private UUID id;

        private String host;

        private LocalDateTime startTime;

        @Enumerated(EnumType.STRING)
        private LiveQuizStatus status;

        @OneToMany(mappedBy = "liveQuiz", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<Question> questions = new ArrayList<>();

        @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
        private List<PlayerScore> playerScores = new ArrayList<>();

        @ElementCollection
        private List<String> players;

        @CreationTimestamp
        private LocalDateTime createdAt;

        @UpdateTimestamp
        private LocalDateTime updatedAt;
}