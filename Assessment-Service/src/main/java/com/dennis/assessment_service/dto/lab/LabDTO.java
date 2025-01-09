package com.dennis.assessment_service.dto.lab;

import com.dennis.assessment_service.model.Enum.AssessmentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class LabDTO {
    private UUID id;
    private String title;
    private AssessmentType assessmentType;
    private byte[] coverImage;
    private LocalDate createdAt;
}
