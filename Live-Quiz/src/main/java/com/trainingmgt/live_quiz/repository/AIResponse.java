package com.trainingmgt.live_quiz.repository;

import com.trainingmgt.live_quiz.request.Prompt;
import com.trainingmgt.live_quiz.response.GeneratedQuestion;

import java.util.List;

public interface AIResponse {
        List<GeneratedQuestion> mapToQuestions(Prompt input);
}
