package com.grading_service.dto;

import com.grading_service.entity.AssessmentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record Graded(UUID id, String title, LocalDateTime dateCreated, AssessmentType type, int traineeCount) {
}