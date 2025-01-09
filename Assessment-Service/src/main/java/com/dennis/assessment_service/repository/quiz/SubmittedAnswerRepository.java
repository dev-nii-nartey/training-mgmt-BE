package com.dennis.assessment_service.repository.quiz;

import com.dennis.assessment_service.model.quiz.SubmittedAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubmittedAnswerRepository extends JpaRepository<SubmittedAnswer, UUID> {
}
