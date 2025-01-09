package com.trainingmgt.live_quiz.repository;

import com.trainingmgt.live_quiz.model.PlayerScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerScoreRepository extends JpaRepository<PlayerScore, UUID> {
    Optional<PlayerScore> findByQuizIdAndUserId(UUID id, String userId);
    List<PlayerScore> findByQuizId(UUID id);
        Optional<PlayerScore> findByUserIdAndQuizId(String playerId, UUID quizId);

}
