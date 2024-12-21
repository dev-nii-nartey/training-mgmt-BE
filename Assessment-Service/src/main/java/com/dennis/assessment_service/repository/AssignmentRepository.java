package com.dennis.assessment_service.repository;

import com.dennis.assessment_service.model.Assignment;
import com.dennis.assessment_service.model.Enum.AssessmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    Optional<Assignment> findByTraineeEmailAndAssessmentId(String traineeEmail, UUID assessmentId);
    List<Assignment> findByTraineeEmail(String traineeEmail);
    List<Assignment> findByAssessmentId(UUID assessmentId);
    boolean existsByTraineeEmailAndAssessmentId(String traineeEmail, UUID assessmentId);
}
