package com.trainingmgt.live_quiz.resource;

import com.trainingmgt.live_quiz.repository.AIResponse;
import com.trainingmgt.live_quiz.request.Prompt;
import com.trainingmgt.live_quiz.response.GeneratedQuestion;

import java.util.*;
import java.util.stream.Collectors;


public record CohereResponse(String text) implements AIResponse {

    @Override
    public List<GeneratedQuestion> mapToQuestions(Prompt prompt) {
        List<GeneratedQuestion> questions = new ArrayList<>();

        List<String> topicTags = splitTopicIntoTags(prompt.getTopic());

        if (prompt.getLevel() != null && !prompt.getLevel().trim().isEmpty()) {
            topicTags.add(prompt.getLevel());
        }

        String[] questionBlocks = this.text.split("(?=question:)");

        for (String block : questionBlocks) {
            block = block.trim();
            if (block.isEmpty()) {
                continue;
            }

            String questionText = "";
            List<String> options = new ArrayList<>();
            String correctAnswer = "";
            int questionIndex = block.indexOf("question:");
            int optionsIndex = block.indexOf("options:");
            int answerIndex = block.indexOf("answer:");

            if (questionIndex != -1 && optionsIndex != -1) {
                questionText = block.substring(questionIndex + 9, optionsIndex).trim();
            }

            if (optionsIndex != -1 && answerIndex != -1) {
                String optionsText = block.substring(optionsIndex + 8, answerIndex).trim(); // Text after "options:" up to "answer:"
                optionsText = optionsText.replace("[", "").replace("]", ""); // Remove square brackets
                String[] optionsArray = optionsText.split(",");
                for (String option : optionsArray) {
                    options.add(option.trim().replaceAll("^'|'$", ""));
                }
            }

            if (answerIndex != -1) {
                correctAnswer = block.substring(answerIndex + 7).trim();
            }

            if (!questionText.isEmpty() && !options.isEmpty() && !correctAnswer.isEmpty()) {
                GeneratedQuestion question = new GeneratedQuestion();
                question.setQuestionText(questionText);
                question.setOptions(options);
                question.setCorrectAnswer(correctAnswer);
                questions.add(question);
            }
        }

        return questions;
    }
    private List<String> splitTopicIntoTags(String topic) {
        Set<String> stopWords = new HashSet<>(Arrays.asList("is", "a", "an", "the", "and", "in", "on", "of", "for", "to", "by", "with"));

        if (topic == null || topic.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(topic.split("\\s+"))
                .map(String::toLowerCase)
                .filter(word -> !stopWords.contains(word))
                .collect(Collectors.toList());
    }

}
