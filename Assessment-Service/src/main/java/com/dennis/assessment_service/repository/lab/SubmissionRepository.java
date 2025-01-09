package com.dennis.assessment_service.repository.lab;

import com.dennis.assessment_service.model.lab.LabSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<LabSubmission, UUID> {
    List<LabSubmission> findByLabId(UUID labId);
}
