package com.dennis.assessment_service.repository.quiz;

import com.dennis.assessment_service.model.quiz.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
