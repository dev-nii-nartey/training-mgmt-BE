package com.grading_service.service;

import com.grading_service.dto.*;
import com.grading_service.entity.SubmittedAssessment;
import com.grading_service.entity.TraineeGradeHistory;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface AssessmentService {
    SubmittedAssessment submitAssessment(AssessmentRequest request) throws IOException;
    SubmittedAssessment gradeQuiz(QuizSubmission request) ;
    SubmittedAssessment gradeAssessment(Grade grade);
     SubmittedAssessment getAssessmentDetails(String title, String email);
    List<TraineeAssessmentDetailsDto> getTraineeGradedAssessment(String traineeId);
    List<SubmittedAssessment> getAssessmentGrades();
    List<Graded> getGradedAssessment();
    List<Graded> getUngradedGradedAssessment();
    List<TraineeGradeHistoryDto> getAllTraineesGradeHistory();
    TraineeGradeHistory getTraineeGradeHistory(String traineeId);
    List<TraineeDto> getAssessmentsByTitle(String title, boolean gradeStatus);
}