package com.dennis.assessment_service.model;

import com.dennis.assessment_service.model.Enum.AssessmentType;
import com.dennis.assessment_service.model.Enum.SubmissionStatus;
import com.dennis.assessment_service.service.AssessmentService;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Trainee email is required")
    @Email(message = "Invalid email format")
    private String traineeEmail;

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    @NotNull(message = "Assessment is required")
    private Assessment assessment;

    @PastOrPresent(message = "Assigned date cannot be in the future")
    private LocalDateTime assignedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Submission status is required")
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @PastOrPresent(message = "Submission date cannot be in the future")
    private LocalDateTime submittedAt;

    @Min(value = 0, message = "Score must be greater than or equal to 0")
    private Integer score;

    @FutureOrPresent(message = "Deadline must be in the future or present")
    private LocalDateTime deadline;
}
