package com.dennis.assessment_service.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class QuizSubmissionDTO {
    private UUID id;
    private String traineeEmail;
    private LocalDateTime submittedAt;
    private int score;
}
