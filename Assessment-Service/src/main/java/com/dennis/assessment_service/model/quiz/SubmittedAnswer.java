package com.dennis.assessment_service.model.quiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedAnswer {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Question id must not be null")
    private UUID questionId;
    @NotNull(message = "Question id must not be null")
    private UUID selectedAnswerId;

    @ManyToOne
    @JoinColumn(name = "quiz_submission_id")
    @JsonBackReference
    private QuizSubmission quizSubmission;
}
