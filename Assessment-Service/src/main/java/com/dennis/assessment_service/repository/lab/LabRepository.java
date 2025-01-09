package com.dennis.assessment_service.repository.lab;

import com.dennis.assessment_service.model.lab.Lab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LabRepository extends JpaRepository<Lab, UUID> {
}
