package com.trainingmgt.live_quiz.repository;

import com.trainingmgt.live_quiz.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer,UUID> {
    List<Answer> findByQuizIdAndQuestionId(UUID id, UUID id1);

   Optional<Answer> findByUserIdAndQuestionId(String userId, UUID id);

    long countByQuizIdAndQuestionId(UUID id, UUID id1);

        List<Answer> findByUserIdAndQuizId(String playerId, UUID quizId);
    }
