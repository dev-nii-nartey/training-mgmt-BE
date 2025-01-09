package com.dennis.assessment_service.dto.lab;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record GradeLabDTO(
        UUID assessmentId,
        String title,
        Long labByWeek,
        List<String> url,
        String traineeId
) {
}
