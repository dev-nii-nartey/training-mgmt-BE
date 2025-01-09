package com.grading_service.service;

import com.grading_service.dto.TraineeDto;
import com.grading_service.entity.SubmittedAssessment;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProfileServiceClient {

    @Value("${profileUrl}")
    private String PROFILE_URL;

    private final WebClient.Builder webClient;

    public ProfileServiceClient(WebClient.Builder webClient) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name = "ExternalServiceBreaker", fallbackMethod = "fallbackCohorts")
    public List<TraineeDto> getTraineesByEmails(Set<String> traineeEmails, Map<String, SubmittedAssessment> assessments) {
       return Flux.fromIterable(traineeEmails)
                .flatMap(email -> webClient
                        .build()
                        .get()
                        .uri(PROFILE_URL + "/fetch/" + email)
                        .retrieve()
                        .bodyToMono(TraineeDto.class)
                        .map(trainee -> {
                            SubmittedAssessment assessment = assessments.get(email);
                            double grade = assessment != null ? assessment.getTotalMarks() : null;
                            assert assessment != null;
                            return new TraineeDto(
                                    trainee.firstName(),
                                    trainee.LastName(),
                                    trainee.specialization(),
                                    grade,
                                    email,
                                    assessment.getUrl()
                            );
                        })
                        .timeout(Duration.ofSeconds(4))
                        .doOnError(e -> log.error("Error fetching cohorts with ID {}: {}", email, e.getMessage()))
                )
                .collectList()
                .block();
    }

    private List<TraineeDto> fallbackCohorts(Set<String> traineeEmails, Map<String, SubmittedAssessment> assessments, Throwable throwable) {
        log.error("Cohort Fallback triggered: {}", throwable.getMessage());
        return traineeEmails.stream()
                .map(email -> {
                    SubmittedAssessment assessment = assessments.get(email);
                    double grade = assessment != null ? assessment.getTotalMarks() : null;
                    assert assessment != null;
                    return new TraineeDto(
                            "FirstName Unavailable",
                            "LastName Unavailable",
                            "Specialization Unavailable",
                            grade,
                            email,
                            assessment.getUrl()
                    );
                })
                .collect(Collectors.toList());
    }
}