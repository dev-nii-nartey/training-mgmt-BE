package com.trainee_management_system.cohorts.service;

import com.trainee_management_system.cohorts.exceptions.InvalidDateRangeException;
import com.trainee_management_system.cohorts.model.Cohort;
import com.trainee_management_system.cohorts.repository.CohortRepository;
import com.trainee_management_system.cohorts.requests.NewCohort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CohortValidationService {
    private final CohortRepository cohortRepository;
    private final int overlapPeriod;

    
    public CohortValidationService(CohortRepository cohortRepository,
            @Value("${services.cohort-management.overlapping-period}") int overlapPeriod) {
        this.cohortRepository = cohortRepository;
        this.overlapPeriod = overlapPeriod;
    }

    public void validateCreate(NewCohort newCohort){
        if (newCohort.getStartDate().isAfter(newCohort.getEndDate())) {
            throw new InvalidDateRangeException("Start date cannot be after end date");
        }
        if (newCohort.getStartDate().isBefore(LocalDate.now())) {
            throw new InvalidDateRangeException("Start date cannot be in the past");
        }
    }

    public void validateUpdate(Cohort cohort, NewCohort updateCohort) {

        if (cohort.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot update a cohort that has already ended.");
        }

        if (cohort.getStartDate().isBefore(LocalDate.now()) && cohort.getEndDate().isAfter(LocalDate.now())) {
            if (!cohort.getStartDate().isEqual(updateCohort.getStartDate())) {
                throw new IllegalStateException("Cannot change the start date of a cohort that has already started.");
            }
        }
        if (updateCohort.getStartDate().isAfter(updateCohort.getEndDate())) {
            throw new InvalidDateRangeException("Start date cannot be after end date");
        }

    }

    public void validateNonOverlappingStartDate(LocalDate startDate)
            throws InvalidDateRangeException {

        if (overlapPeriod == 0) {
            return;
        }

        List<Cohort> overlappingCohorts = cohortRepository.findCohortsWithinRange(
                startDate.minusMonths(overlapPeriod),
                startDate.plusMonths(overlapPeriod));

        if (!overlappingCohorts.isEmpty()) {
            throw new InvalidDateRangeException("A cohort is in-progress within the specified period.");
        }
    }
}