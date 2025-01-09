package com.grading_service.repository;

import com.grading_service.entity.SubmittedAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<SubmittedAssessment,Long> {
    List<SubmittedAssessment> findByTraineeEmailAndGraded(String traineeId, boolean trueOrFalse);
    List<SubmittedAssessment> findAssessmentsByGradedIs(boolean trueOrFalse);
    long countDistinctTraineeIdByGradedIs(boolean trueOrFalse);
    List<SubmittedAssessment> findByTitleIgnoreCaseAndGradedIs(String title, boolean trueOrFalse);
    SubmittedAssessment findByTitleAndTraineeEmailIgnoreCase(String title, String traineeId);

    List<SubmittedAssessment> findSubmittedAssessmentByTitle(String title);
}