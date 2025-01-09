package com.trainingmgt.live_quiz.repository;

import com.trainingmgt.live_quiz.model.LiveQuiz;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface QuizRepository extends CrudRepository<LiveQuiz, UUID> {
}
