package com.grading_service.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public record AssessmentRequest (
        UUID assessmentId,
        String title,
        Long labByWeek,
        List<String> url,
        String traineeId,
        MultipartFile file
){
}