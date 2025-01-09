package com.grading_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeGradeHistoryDto {
    private Long id;
    private String traineeEmail;
    private String firstName;
    private String lastName;
    private double overallGradePoints;
    private int gradedQuizzesCount;
    private int gradedLabsCount;
    private int gradedPresentationsCount;
    private double averageGradePoints;
}