package com.dennis.assessment_service.service;

import com.dennis.assessment_service.dto.lab.GradeLabDTO;
import com.dennis.assessment_service.dto.quiz.GradeQuizDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class GradeService {

    private final WebClient.Builder webClientBuilder;

    @Value("${grade.service.url}")
    private String gradeServiceUrl;




    public GradeService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "gradeService", fallbackMethod = "fallbackForGradeService")
    @RateLimiter(name = "gradeServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public String sendQuizToGradeService(GradeQuizDTO gradeQuizDTO) {
        log.warn("Webclient in action");

        return webClientBuilder.build()
                .post()
                .uri(gradeServiceUrl +"/api/v1/assessments/submit-quiz")
                .body(Mono.just(gradeQuizDTO), GradeQuizDTO.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new RuntimeException("Error: Unable to send quiz to grade service. Try again later." )))
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(4))
                .block();
    }


    @CircuitBreaker(name = "gradeService", fallbackMethod = "fallbackForGradeService")
    @RateLimiter(name = "gradeServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public void sendLabToGradeService(GradeLabDTO gradeLabDTO) {
        log.warn("Webclient in action");
        String response = webClientBuilder.build()
                .post()
                .uri(gradeServiceUrl +"/api/v1/assessments/submit-lab")
                .body(Mono.just(gradeLabDTO), GradeLabDTO.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new RuntimeException("Error: Unable to send lab to grade service. Try again later. " )))
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(4))
                .block();
        log.info("Response: {}", response);
    }

    public void fallbackForGradeService(Object dto, Throwable throwable) {
        log.error("CircuitBreaker Fallback executed. DTO: {}, Error: {}", dto, throwable.getMessage());
        //TODO: send dto to a message broker
    }

    public void rateLimiterFallback(Object dto, Throwable throwable) {
        log.error("RateLimiter Fallback executed. DTO: {}, Error: {}", dto, throwable.getMessage());
    }

}
