package com.trainee_management_system.cohorts.service;

import com.trainee_management_system.cohorts.resources.Trainee;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserProfileService {

    @Value("${services.user-profile.base-url}")
    private String userProfileBaseUrl;

    private final WebClient.Builder webClientBuilder;

    public UserProfileService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "ExternalServiceBreaker", fallbackMethod = "fallbackTrainees")
    public List<Trainee> getTraineesByCohortId(Long cohortId) {
        return webClientBuilder
                .build()
                .get()
                .uri(userProfileBaseUrl + "/profiles/trainees/cohort/" + cohortId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Trainee>>() {
                })
                .timeout(Duration.ofSeconds(4))
                .doOnError(e -> log.error("Error fetching trainees for cohort ID {}: {}", cohortId, e.getMessage()))
                .block();
    }

    private List<Trainee> fallbackTrainees(Long cohortId, Throwable throwable) {
        log.error("Fallback triggered for cohort ID {}: {}", cohortId, throwable.getMessage());
        return Collections.singletonList(Trainee.builder()
                .id(-1L)
                .fullName("N/A")
                .email("N/A")
                .contact("N/A")
                .status("N/A")
                .specializationName("N/A")
                .dateCreated(LocalDate.now())
                .build());
    }


}