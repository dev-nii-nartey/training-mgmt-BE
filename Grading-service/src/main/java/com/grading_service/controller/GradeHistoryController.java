package com.grading_service.controller;


import com.grading_service.dto.TraineeAssessmentDetailsDto;
import com.grading_service.dto.TraineeDto;
import com.grading_service.dto.TraineeGradeHistoryDto;
import com.grading_service.service.AssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/grades")
public class GradeHistoryController {

    private final AssessmentService assessmentService;

    public GradeHistoryController(AssessmentService gradeHistoryService) {
        this.assessmentService = gradeHistoryService;
    }


    @GetMapping("/history")
    public ResponseEntity<List<TraineeGradeHistoryDto>> getAllTraineesGradeHistory(
    ) {
        return ResponseEntity.ok(assessmentService.getAllTraineesGradeHistory());
    }

    @PostMapping("/history")
    public ResponseEntity<List<TraineeAssessmentDetailsDto>> getTraineeGradedAssessment(@RequestBody TraineeDto traineeId) {
        List<TraineeAssessmentDetailsDto> submittedAssessment = assessmentService.getTraineeGradedAssessment(traineeId.email());
        return ResponseEntity.ok(submittedAssessment);
    }

}
