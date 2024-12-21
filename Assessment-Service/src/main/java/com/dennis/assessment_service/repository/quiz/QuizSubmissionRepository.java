package com.dennis.assessment_service.repository.quiz;

import com.dennis.assessment_service.model.quiz.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, UUID> {
    List<QuizSubmission> findByQuizId(UUID quizId);
}
