package com.trainingmgt.live_quiz.resource;

import com.trainingmgt.live_quiz.repository.AIResponse;
import com.trainingmgt.live_quiz.request.Prompt;
import com.trainingmgt.live_quiz.response.GeneratedQuestion;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;


public record AIMLResponse(String id,String object,List<Choice> choices) implements AIResponse {

    @Override
    public List<GeneratedQuestion> mapToQuestions(Prompt prompt) {
        List<GeneratedQuestion> questions = new ArrayList<>();

        List<String> topicTags = splitTopicIntoTags(prompt.getTopic());

        if (prompt.getLevel() != null && !prompt.getLevel().trim().isEmpty()) {
            topicTags.add(prompt.getLevel());
        }

        String[] questionBlocks = this.choices.getFirst().getMessage().getContent().split("\n\n");

        for (String block : questionBlocks) {
            if (block.trim().isEmpty()) {
                continue;
            }

            String[] lines = block.split("\\n");
            if (lines.length < 2) {
                continue;
            }

            String questionText = "";
            List<String> options = new ArrayList<>();
            String correctAnswer = "";

            for (String line : lines) {
                line = line.trim();

                if (line.toLowerCase().startsWith("question:")) {
                    questionText = line.replaceFirst("question:\\s*", "").trim();
                } else if (line.toLowerCase().startsWith("options:")) {
                    String optionsString = line.replaceFirst("options:\\s*", "").trim();
                    optionsString = optionsString.substring(1, optionsString.length() - 1); // Remove surrounding brackets []
                    String[] parsedOptions = optionsString.split("(?<!\\\\),"); // Split by commas not preceded by escape
                    for (String option : parsedOptions) {
                        options.add(option.replaceAll("['/\"\\\\]", "").trim());
                    }
                } else if (line.toLowerCase().startsWith("answer:")) {
                    correctAnswer = line.replaceFirst("answer:\\s*", "").trim();
                }
            }

            GeneratedQuestion question = new GeneratedQuestion();
            question.setQuestionText(questionText);
            question.setOptions(options);
            question.setCorrectAnswer(correctAnswer);

            question.setTags(topicTags);

            questions.add(question);
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



    @Setter
    @Getter
    public static class Choice {
        private int index;
        private String finish_reason;
        private Message message;

    }

    @Setter
    @Getter
    public static class Message {
        private String role;
        private String content;
    }

}