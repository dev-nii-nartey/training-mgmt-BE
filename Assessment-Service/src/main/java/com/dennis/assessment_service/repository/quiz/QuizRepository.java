package com.dennis.assessment_service.repository.quiz;

import com.dennis.assessment_service.model.quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    @Query("SELECT COUNT(q.id) FROM Question q WHERE q.quiz.id = :quizId")
    int countQuestionsByQuizId(@Param("quizId") UUID quizId);
}
