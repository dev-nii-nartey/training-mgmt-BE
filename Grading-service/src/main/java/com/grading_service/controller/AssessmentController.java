package com.grading_service.controller;


import com.grading_service.dto.*;
import com.grading_service.entity.SubmittedAssessment;
import com.grading_service.service.AssessmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@AllArgsConstructor
@Slf4j
public class AssessmentController {

    private AssessmentService assessmentService;


    @PostMapping("/submit-lab")
    public ResponseEntity<SubmittedAssessment> submitAssessment(@ModelAttribute AssessmentRequest request) throws IOException {
        SubmittedAssessment submittedAssessment = assessmentService.submitAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(submittedAssessment);
    }


    @PostMapping("/submit-quiz")
    public ResponseEntity<String> gradeQuiz(@RequestBody QuizSubmission request) {
        SubmittedAssessment submittedAssessment = assessmentService.gradeQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(submittedAssessment.getLetterGrade());
    }


    @PostMapping("/grade")
    public ResponseEntity<SubmittedAssessment> gradeAssessment(@RequestBody Grade grade) {
        SubmittedAssessment submittedAssessment = assessmentService.gradeAssessment(grade);
        return ResponseEntity.ok(submittedAssessment);
    }

    @PostMapping("/get-assessment")
    public ResponseEntity<SubmittedAssessment> getAssessmentDetails(@RequestBody GetAssessment grade) {
        SubmittedAssessment submittedAssessment = assessmentService.getAssessmentDetails(grade.assessmentTitle(),grade.traineeEmail());
        return ResponseEntity.ok(submittedAssessment);
    }


    @GetMapping("/graded")
    public ResponseEntity<List<Graded>> getGradedAssessments() {
        return ResponseEntity.ok(assessmentService.getGradedAssessment());
    }


    @GetMapping("/ungraded")
    public ResponseEntity<List<Graded>> getUngradedAssessments() {
        return ResponseEntity.ok(assessmentService.getUngradedGradedAssessment());
    }


    @PostMapping("/submitted")
    public ResponseEntity<List<TraineeDto>> getAssessmentsByTitle(@RequestBody Finder find) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByTitle(find.title(), find.graded()));
    }

}