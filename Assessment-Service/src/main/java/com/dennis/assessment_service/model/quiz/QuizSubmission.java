package com.dennis.assessment_service.model.quiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizSubmission {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Trainee email is required")
    @Email(message = "Invalid email format")
    private String traineeEmail;

    @NotNull(message = "Submission time is required")
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Min(value = 0, message = "Score cannot be negative")
    private int score;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonBackReference
    private Quiz quiz;

    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Size(min = 1, message = "There must be at least one submitted answer")
    private List<SubmittedAnswer> submittedAnswers;
}
