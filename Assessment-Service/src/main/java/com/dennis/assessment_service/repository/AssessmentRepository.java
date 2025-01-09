package com.dennis.assessment_service.repository;

import com.dennis.assessment_service.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
    Optional<Assessment> findById(UUID id);
}
