package com.grading_service.dto;

import java.util.UUID;

public record QuizSubmission(
        UUID quizId,
        String traineeId,
        String title,
        Double totalMarks){

}