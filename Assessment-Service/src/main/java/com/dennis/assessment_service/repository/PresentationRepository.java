package com.dennis.assessment_service.repository;

import com.dennis.assessment_service.model.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PresentationRepository extends JpaRepository<Presentation, UUID> {
}
