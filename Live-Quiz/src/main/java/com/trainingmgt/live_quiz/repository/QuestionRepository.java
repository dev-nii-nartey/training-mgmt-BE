package com.trainingmgt.live_quiz.repository;

import com.trainingmgt.live_quiz.model.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface QuestionRepository extends CrudRepository<Question, UUID> {}