package com.trainingmgt.live_quiz.repository;

import com.trainingmgt.live_quiz.request.Prompt;
import com.trainingmgt.live_quiz.response.GeneratedQuestion;

public interface QuestionGenerationStrategy {

    Iterable<GeneratedQuestion> generateMultipleChoiceQuestions(Prompt input);

    Iterable<GeneratedQuestion> generateTrueFalseQuestions(Prompt input);

    Iterable<GeneratedQuestion> generateImageQuestions(Prompt input);

}
