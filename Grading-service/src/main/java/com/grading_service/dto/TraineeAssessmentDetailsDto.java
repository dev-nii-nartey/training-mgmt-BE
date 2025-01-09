package com.grading_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TraineeAssessmentDetailsDto {
    private Long id;
    private UUID assessmentId;
    private String title;
    private String type;
    private String traineeEmail;
    private String firstName;
    private String lastName;
    private String specialization;
    private LocalDateTime dateSubmitted;
    private double totalMarks;
    private String letterGrade;
    private boolean graded;

}