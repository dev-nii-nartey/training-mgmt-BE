package com.trainingmgt.live_quiz.service;

import com.trainingmgt.live_quiz.model.LiveQuiz;
import com.trainingmgt.live_quiz.model.Question;
import com.trainingmgt.live_quiz.repository.QuestionGenerationStrategy;
import com.trainingmgt.live_quiz.repository.QuizRepository;
import com.trainingmgt.live_quiz.request.Prompt;
import com.trainingmgt.live_quiz.response.GeneratedQuestion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionGenerationFallbackService implements QuestionGenerationStrategy {

    private final QuizRepository quizRepository;

    public QuestionGenerationFallbackService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public List<GeneratedQuestion> generateMultipleChoiceQuestions(Prompt input) {
        Iterable<LiveQuiz> quizzes = quizRepository.findAll();
        List<GeneratedQuestion> generatedQuestions = new ArrayList<>();

        List<String> topicWords = input.getTopic() != null ? splitTopicIntoWords(input.getTopic()) : new ArrayList<>();

        for (LiveQuiz quiz : quizzes) {
            for (Question question : quiz.getQuestions()) {
//                if (question.getType() == null || !question.getType().toString().equalsIgnoreCase(input.getQuestionType())) {
//                    continue;
//                }
                if (input.getTopic() != null && !isTopicMatch(question, topicWords)) {
                    continue;
                }

                if (input.getLevel() != null && !question.getTags().contains(input.getLevel())) {
                    continue;
                }

                GeneratedQuestion generatedQuestion = new GeneratedQuestion();
                generatedQuestion.setQuestionText(question.getQuestionText());
                generatedQuestion.setOptions(question.getOptions());
                generatedQuestion.setCorrectAnswer(question.getCorrectAnswer());
                generatedQuestions.add(generatedQuestion);
            }
        }

        return generatedQuestions.stream()
                .sorted((q1, q2) -> {
                    int q1Score = calculateWeightedMatchScore(q1, input, topicWords);
                    int q2Score = calculateWeightedMatchScore(q2, input, topicWords);
                    return Integer.compare(q2Score, q1Score);
                })
                .limit(50)
                .collect(Collectors.toList());
    }

    @Override
    public List<GeneratedQuestion> generateTrueFalseQuestions(Prompt input) {
        return generateMultipleChoiceQuestions(input);
    }

    @Override
    public List<GeneratedQuestion> generateImageQuestions(Prompt input) {
        return generateMultipleChoiceQuestions(input);
    }

    private List<String> splitTopicIntoWords(String topic) {
        return List.of(topic.split("\\s+"))
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private boolean isTopicMatch(Question question, List<String> topicWords) {
        if (topicWords.isEmpty()) return true;
        return question.getTags().stream()
                .map(String::toLowerCase)
                .anyMatch(tag -> topicWords.stream().anyMatch(tag::contains));
    }

    private int calculateWeightedMatchScore(GeneratedQuestion question, Prompt prompt, List<String> topicWords) {
        int score = 0;

        int topicWeight = 5;
        int levelWeight = 3;
        int questionTypeWeight = 2;

        if (prompt.getQuestionType() != null && question.getQuestionText().contains(prompt.getQuestionType())) {
            score += questionTypeWeight;
        }

        if (prompt.getTopic() != null) {
            int topicMatchCount = (int) topicWords.stream()
                    .filter(word -> question.getQuestionText().toLowerCase().contains(word))
                    .count();
            score += topicWeight * topicMatchCount;
        }

        if (prompt.getLevel() != null && question.getQuestionText().contains(prompt.getLevel())) {
            score += levelWeight;
        }

        return score;
    }
}
