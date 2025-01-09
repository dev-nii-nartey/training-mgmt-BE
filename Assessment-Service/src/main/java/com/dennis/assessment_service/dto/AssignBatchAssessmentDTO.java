package com.dennis.assessment_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AssignBatchAssessmentDTO {
    @NotEmpty(message = "Trainee emails must not be empty")
    private List<@Email(message = "Invalid email format") String> traineeEmail;
    @NotNull(message = "Assessment ID must not be null")
    private UUID assessmentId;
    @FutureOrPresent(message = "Deadline must be in the future or present")
    private LocalDateTime deadline;

}
