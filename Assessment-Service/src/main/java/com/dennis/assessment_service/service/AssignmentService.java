package com.dennis.assessment_service.service;

import com.dennis.assessment_service.dto.AssessmentDTO;
import com.dennis.assessment_service.dto.CohortResponseDto;
import com.dennis.assessment_service.model.Assessment;
import com.dennis.assessment_service.model.Enum.SubmissionStatus;
import com.dennis.assessment_service.model.Assignment;
import com.dennis.assessment_service.repository.AssessmentRepository;
import com.dennis.assessment_service.repository.AssignmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final AssessmentRepository assessmentRepository;
    private final CohortService cohortService;


    public AssignmentService(AssignmentRepository assignmentRepository, AssessmentRepository assessmentRepository, CohortService cohortService) {
        this.assignmentRepository = assignmentRepository;
        this.assessmentRepository = assessmentRepository;
        this.cohortService = cohortService;
    }

    @Transactional
    public void assignAssessment(String traineeEmail, UUID assessmentId, LocalDateTime deadline) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assessment with ID " + assessmentId + " not found"));

        Assignment assignment = new Assignment();
        assignment.setTraineeEmail(traineeEmail);
        assignment.setAssessment(assessment);
        assignment.setDeadline(deadline);

        assignmentRepository.save(assignment);
    }

    @Transactional
    public void assignAssessmentToMultipleTrainees(List<String> traineeEmails, UUID assessmentId, LocalDateTime deadline) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assessment with ID " + assessmentId + " not found"));

        List<Assignment> assignments = traineeEmails.stream().map(traineeEmail -> {
            Assignment assignment = new Assignment();
            assignment.setTraineeEmail(traineeEmail);
            assignment.setAssessment(assessment);
            assignment.setDeadline(deadline);
            return assignment;
        }).toList();

        assignmentRepository.saveAll(assignments);
        //TODO: send trainee assignment email
    }

    @Transactional
    public void assignAssessmentToCohort(Long cohortId, UUID assessmentId, LocalDateTime deadline){
        List<CohortResponseDto> cohortResponseDtos = cohortService.getTrainees(cohortId);
       List<String> emails = cohortResponseDtos.stream()
                .map(CohortResponseDto::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .toList();
        assignAssessmentToMultipleTrainees(emails, assessmentId, deadline);
    }

    @Transactional
    public void markAsSubmitted(String traineeEmail, UUID assessmentId, int score) {
        Assignment assignment = assignmentRepository.findByTraineeEmailAndAssessmentId(traineeEmail, assessmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Assignment for trainee " + traineeEmail + " and assessment ID " + assessmentId + " not found"));

        assignment.setStatus(SubmissionStatus.SUBMITTED);
        assignment.setSubmittedAt(LocalDateTime.now());
        assignment.setScore(score);

        assignmentRepository.save(assignment);
    }

    @Transactional
    public void markAsSubmitted(String traineeEmail, UUID assessmentId) {
        Assignment assignment = assignmentRepository.findByTraineeEmailAndAssessmentId(traineeEmail, assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        assignment.setStatus(SubmissionStatus.SUBMITTED);
        assignment.setSubmittedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);
    }


    @Transactional(readOnly = true)
    public List<AssessmentDTO> getAssignmentsByTrainee(String traineeEmail) {
        List<Assignment> assignments = assignmentRepository.findByTraineeEmail(traineeEmail);
        return assignments.stream()
                .map(assignment -> convertToAssessmentDTO(assignment.getAssessment()))
                .collect(Collectors.toList());
    }

    private AssessmentDTO convertToAssessmentDTO(Assessment assessment) {
        return AssessmentDTO.builder()
                .id(assessment.getId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .focusArea(assessment.getFocusArea())
                .coverImage(assessment.getCoverImage())
                .assessmentType(assessment.getAssessmentType())
                .build();
    }


    @Transactional(readOnly = true)
    public List<Assignment> getAssignmentsByAssessment(UUID assessmentId) {
        return assignmentRepository.findByAssessmentId(assessmentId);
    }

    @Transactional(readOnly = true)
    public boolean existsByTraineeEmailAndAssessmentId(String traineeEmail, UUID quizId) {
        return assignmentRepository.existsByTraineeEmailAndAssessmentId(traineeEmail, quizId);
    }
}
