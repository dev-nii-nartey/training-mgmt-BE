package com.dennis.assessment_service.dto;

import com.dennis.assessment_service.model.Enum.AssessmentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class AssessmentDTO {
    private UUID id;
    private String title;
    private String focusArea;
    private String description;
    private byte[] coverImage;
    private AssessmentType assessmentType;
    private LocalDateTime createdAt;
}
