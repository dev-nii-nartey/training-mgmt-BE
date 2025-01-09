package com.trainee_management_system.cohorts.jobs;

import com.trainee_management_system.cohorts.enums.CohortStatus;
import com.trainee_management_system.cohorts.model.Cohort;
import com.trainee_management_system.cohorts.repository.CohortRepository;
import com.trainee_management_system.cohorts.resources.Trainee;
import com.trainee_management_system.cohorts.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class StatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StatusScheduler.class);
    private final CohortRepository cohortRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserProfileService userProfile;

    public StatusScheduler(CohortRepository cohortRepository, RabbitTemplate rabbitTemplate,  UserProfileService userProfile) {
        this.cohortRepository = cohortRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.userProfile = userProfile;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateCohortStatuses() {
        LocalDate today = LocalDate.now();
        logger.info("Cohort status update job started at {}", today);

        CompletableFuture<Void> activateTask = updateCohortStatusAsync(
                cohortRepository.findCohortStartingTodayInactive(), CohortStatus.ACTIVE, "Activated");

        CompletableFuture<Void> deactivateTask = updateCohortStatusAsync(
                cohortRepository.findCohortEndingTodayActive(), CohortStatus.INACTIVE, "Deactivated");

        CompletableFuture.allOf(activateTask, deactivateTask).join();

        logger.info("Cohort status update job completed at {}", LocalDate.now());
    }

    private CompletableFuture<Void> updateCohortStatusAsync(
            List<Cohort> cohorts, CohortStatus status, String actionType) {
        return CompletableFuture.runAsync(() -> {
            try {
                cohorts.forEach(cohort -> cohort.setStatus(status));
                cohortRepository.saveAll(cohorts);
                publishUpdatedCohorts(cohorts, status);
                logger.info("{} {} cohort(s) to status {}", actionType, cohorts.size(), status);
            } catch (Exception e) {
                logger.error("Error updating {} cohorts to status {}: {}", actionType, status, e.getMessage(), e);
            }
        });
    }

    private void publishUpdatedCohorts(List<Cohort> cohorts, CohortStatus status) {
        List<UpdatedCohort> cohortDetailsList = cohorts.stream()
                .map(cohort -> {
                    List<Long> traineeIds = fetchTraineeIds(cohort.getId());
                    return new UpdatedCohort(
                            cohort.getId(),
                            cohort.getName(),
                            traineeIds,
                            status
                    );
                })
                .collect(Collectors.toList());

        if (!cohortDetailsList.isEmpty()) {
            try {
                // Publish to the first queue
                rabbitTemplate.convertAndSend("cohort-status-exchange", "cohort.status.update", cohortDetailsList);
                logger.info("Published {} cohort(s) with status {} to RabbitMQ (Queue 1).", cohortDetailsList.size(), status);

                // Publish to the second queue
                rabbitTemplate.convertAndSend("cohort-status-exchange", "cohort.status.secondary", cohortDetailsList);
                logger.info("Published {} cohort(s) with status {} to RabbitMQ (Queue 2).", cohortDetailsList.size(), status);
            } catch (Exception e) {
                logger.error("Failed to publish cohort details to RabbitMQ: {}", e.getMessage(), e);
            }
        }
    }
    private List<Long> fetchTraineeIds(Long cohortId) {
        try {
            List<Trainee> trainees = userProfile.getTraineesByCohortId(cohortId);

            return trainees != null
                    ? trainees.stream()
                    .map(Trainee::getId)
                    .collect(Collectors.toList())
                    : List.of();
        } catch (Exception e) {
            logger.error("Failed to fetch trainee IDs for cohort {}: {}", cohortId, e.getMessage(), e);
            return List.of();
        }
    }


    record UpdatedCohort(Long cohortId, String name, List<Long> traineeIds, CohortStatus status) {
    }
}
