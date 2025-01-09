package com.trainingmgt.live_quiz.service.AI;

import com.cohere.api.Cohere;
import com.cohere.api.requests.ChatRequest;
import com.cohere.api.types.ChatMessage;
import com.cohere.api.types.Message;
import com.trainingmgt.live_quiz.repository.QuestionGenerationStrategy;
import com.trainingmgt.live_quiz.request.Prompt;
import com.trainingmgt.live_quiz.resource.CohereResponse;
import com.trainingmgt.live_quiz.response.GeneratedQuestion;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Primary
public class CohereAI implements QuestionGenerationStrategy {
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    Cohere cohere = Cohere.builder()
            .token("mUgXA6I8UxytUq6kN16H8VJXDbQEELwR4Lz99Vxu")
            .clientName("snippet").build();

    @Override
    @CircuitBreaker(name = "LiveQuizBreaker", fallbackMethod = "questionGenerationFallbackService.generateMultipleChoiceQuestions")
    public Iterable<GeneratedQuestion> generateMultipleChoiceQuestions(Prompt prompt) {
            String dynamicPrompt = String.format(
                    "Generate 10 '%s' multiple-choice question on the topic of '%s'.Question should have one correct answer and three distractors. Give as 'question:', 'options:[]', and 'answer:' no question numbers,NO LABELS ON THE OPTIONS.",
                    prompt.getLevel(), prompt.getTopic()
            );

        ChatRequest request = ChatRequest.builder()
                .message(dynamicPrompt)
                .chatHistory(
                        List.of(
                                Message.user(ChatMessage.builder().message("Please create multiple-choice questions based on the specified parameters. ").build()),
                                Message.chatbot(ChatMessage.builder().message("Sure, I'll generate the questions. Please provide the details.").build())
                        )
                )
                .build();
        CohereResponse response =new CohereResponse(cohere.chat(request).getText());

        return response.mapToQuestions(prompt);
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
