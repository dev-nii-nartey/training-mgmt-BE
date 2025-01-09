package com.trainingmgt.live_quiz.service.AI;

import com.trainingmgt.live_quiz.service.QuestionGenerationFallbackService;
import com.trainingmgt.live_quiz.request.Prompt;
import com.trainingmgt.live_quiz.resource.AIMLResponse;
import com.trainingmgt.live_quiz.response.GeneratedQuestion;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class AIML {
    private static final Logger logger = LoggerFactory.getLogger(AIML.class);
    private final QuestionGenerationFallbackService questionGenerationFallbackService;

    private final WebClient.Builder webClientBuilder;

    @Value("${ai.keys.aiml}")
    private String apiKey;
    @Value("${ai.urls.aiml}")
    private String apiUrl;

    public AIML(QuestionGenerationFallbackService questionGenerationFallbackService, WebClient.Builder webClientBuilder){
        this.questionGenerationFallbackService = questionGenerationFallbackService;
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "LiveQuizBreaker", fallbackMethod = "generateMultipleChoiceQuestionsFallback")
    public List<GeneratedQuestion> generateMultipleChoiceQuestions(Prompt prompt) {
        String input = buildPrompt(prompt, "Create 7 %s multiple-choice questions. Question should have one correct answer and three distractors. " +
                "Give as 'question:', 'options:[]', and 'answer:' no question numbers, NO LABELS ON THE OPTIONS.");
        return getGeneratedQuestions(input,prompt);
    }

    @org.jetbrains.annotations.NotNull
    private List<GeneratedQuestion> getGeneratedQuestions(String input,Prompt prompt) {
        AIMLResponse response = webClientBuilder
                .build().post()
                .uri(apiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(input)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                    // Log error status and body
                    clientResponse.bodyToMono(String.class).doOnTerminate(() -> {
                        logger.error("Error response: Status {}. Response body: {}",
                                clientResponse.statusCode(), clientResponse.toString());
                    }).subscribe();
                    return clientResponse.createException();
                })
                .bodyToMono(AIMLResponse.class)
                .block();
        assert response != null;
        return response.mapToQuestions(prompt);
    }

    @CircuitBreaker(name = "LiveQuizBreaker", fallbackMethod = "generateTrueFalseQuestionsFallback")
    public List<GeneratedQuestion> generateTrueFalseQuestions(Prompt prompt) {
        String input = buildPrompt(prompt, "Create 7 %s True/False questions. Give response as 'question:','options:[]', and 'answer:' without question numbers.");
        return getGeneratedQuestions(input,prompt);
    }

    @CircuitBreaker(name = "LiveQuizBreaker", fallbackMethod = "generateImageQuestionsFallback")
    public List<GeneratedQuestion> generateImageQuestions(Prompt prompt) {
        String input = buildPrompt(prompt, "Create 5 %s multiple-choice questions. Question should have one correct answer and three distractors. " +
                "Give as 'question:', 'options:[]', and 'answer:' no question numbers, NO LABELS ON THE OPTIONS.");
        return getGeneratedQuestions(input,prompt);
    }

    @NotNull
    private String buildPrompt(Prompt input, String template) {
        return String.format(
                """
                {
                  "model": "gpt-4o",
                  "messages": [
                    {
                      "role": "system",
                      "content": "You are an expert %s quiz creator."
                    },
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """,
                input.getTopic(), String.format(template, input.getLevel())
        );
    }

    public List<GeneratedQuestion> generateMultipleChoiceQuestionsFallback(Prompt input,Throwable throwable) {
        logger.info("Fallback method triggered for generateMultipleChoiceQuestions.");
        return questionGenerationFallbackService.generateMultipleChoiceQuestions(input);
    }

    public List<GeneratedQuestion> generateTrueFalseQuestionsFallback(Prompt input, Throwable throwable) {
        logger.info("Fallback method triggered for generateTrueFalseQuestions.");
        return questionGenerationFallbackService.generateTrueFalseQuestions(input);
    }

    public List<GeneratedQuestion> generateImageQuestionsFallback(Prompt input,Throwable throwable) {
        logger.info("Fallback method triggered for generateImageQuestions.");
        return questionGenerationFallbackService.generateImageQuestions(input);
    }

}
