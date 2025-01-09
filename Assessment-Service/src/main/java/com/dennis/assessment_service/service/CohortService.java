package com.dennis.assessment_service.service;

import com.dennis.assessment_service.dto.CohortResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class CohortService {
    private final WebClient.Builder webClientBuilder;

//    @Value("${user.profile.service.url}")
//    private String ProfileServiceUrl;



    public CohortService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "profileService", fallbackMethod = "fallbackForProfileService")
    @RateLimiter(name = "profileServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public List<CohortResponseDto> getTrainees(Long cohortId) {
        log.warn("WebClient in action");
        if (cohortId == null || cohortId <= 0) {
            throw new IllegalArgumentException("Invalid cohortId provided");
        }
        List<CohortResponseDto> cohortResponseDtos = webClientBuilder.build()
                .get()
                .uri("lb://USER-PROFILE-SERVICE/api/v1/profile/trainees/cohort/" + cohortId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new RuntimeException("Unable to fetch trainees in this cohort. Try again later")))
                )
                .bodyToMono(new ParameterizedTypeReference<List<CohortResponseDto>>() {})
                .timeout(Duration.ofSeconds(4))
                .block();
        log.info("Response: {}", cohortResponseDtos);
        return cohortResponseDtos;
    }

    public void fallbackForProfileService(CohortResponseDto cohortResponseDto, Throwable ex) {
        log.error("Fallback triggered for communication with profile service: {}", ex.getMessage());
        log.error("Executing fallback method");
    }

    public void rateLimiterFallback(CohortResponseDto cohortResponseDto, Throwable ex) {
        log.error("Rate limiter fallback triggered {}", ex.getMessage());
    }

}