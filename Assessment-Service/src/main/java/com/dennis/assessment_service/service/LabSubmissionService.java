package com.dennis.assessment_service.service;

import com.dennis.assessment_service.dto.lab.GradeLabDTO;
import com.dennis.assessment_service.model.lab.Lab;
import com.dennis.assessment_service.model.lab.LabSubmission;
import com.dennis.assessment_service.repository.lab.LabRepository;
import com.dennis.assessment_service.repository.lab.SubmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LabSubmissionService {
    private final SubmissionRepository submissionRepository;
    private final LabRepository labRepository;
    private final AssignmentService assignmentService;
    private final GradeService gradeService;

    public LabSubmissionService(SubmissionRepository submissionRepository, LabRepository labRepository, AssignmentService assignmentService, GradeService gradeService) {
        this.submissionRepository = submissionRepository;
        this.labRepository = labRepository;
        this.assignmentService = assignmentService;
        this.gradeService = gradeService;
    }

    @Transactional
    public LabSubmission submitLab(UUID labId, LabSubmission labSubmission) {
        Lab lab = labRepository.findById(labId).orElseThrow(() -> new EntityNotFoundException("Lab with ID " + labId + " not found"));

        boolean isAssigned = assignmentService.existsByTraineeEmailAndAssessmentId(labSubmission.getTraineeEmail(), labId);
        if (!isAssigned) {
            throw new EntityNotFoundException("Trainee has not been assigned this lab");
        }
        labSubmission.setLab(lab);
        assignmentService.markAsSubmitted(labSubmission.getTraineeEmail(), labId);

        GradeLabDTO gradeLabDTO = GradeLabDTO.builder()
                .assessmentId(labId)
                .title(lab.getTitle())
                .labByWeek((long) labSubmission.getLabWeek())
                .url(labSubmission.getSubmissionLinks())
                .traineeId(labSubmission.getTraineeEmail())
                .build();
        gradeService.sendLabToGradeService(gradeLabDTO);


        return submissionRepository.save(labSubmission);
    }

    @Transactional(readOnly = true)
    public List<LabSubmission> getSubmissionsByLabId(UUID labId) {
        return submissionRepository.findByLabId(labId);
    }
}
