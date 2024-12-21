package com.trainingmgt.live_quiz.service.AI;

import com.trainingmgt.live_quiz.repository.QuestionGenerationStrategy;
import com.trainingmgt.live_quiz.request.Prompt;
import com.trainingmgt.live_quiz.response.GeneratedQuestion;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class AmaliAI implements QuestionGenerationStrategy {

    @Override
    @CircuitBreaker(name = "LiveQuizBreaker", fallbackMethod = "questionGenerationFallbackService.generateMultipleChoiceQuestions")

    public Iterable<GeneratedQuestion> generateMultipleChoiceQuestions(Prompt input) {
        return null;
    }

    @Override
    @CircuitBreaker(name = "LiveQuizBreaker", fallbackMethod = "questionGenerationFallbackService.generateTrueFalseQuestions")
    public Iterable<GeneratedQuestion> generateTrueFalseQuestions(Prompt input) {
        return null;
    }

    @Override
    @CircuitBreaker(name = "LiveQuizBreaker", fallbackMethod = "questionGenerationFallbackService.generateImageBasedQuestions")
    public Iterable<GeneratedQuestion> generateImageQuestions(Prompt input) {
        return null;
    }

}
