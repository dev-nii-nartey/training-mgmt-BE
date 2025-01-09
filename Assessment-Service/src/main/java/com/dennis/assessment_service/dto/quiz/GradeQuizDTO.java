package com.dennis.assessment_service.dto.quiz;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GradeQuizDTO(
        UUID quizId,
        String traineeId,
        String title,
        Double totalMarks
) {
}
