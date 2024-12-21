package com.dennis.assessment_service.repository.quiz;

import com.dennis.assessment_service.model.quiz.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
}
