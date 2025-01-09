package com.trainee_management_system.cohorts.service;

import com.trainee_management_system.cohorts.exceptions.InvalidDateRangeException;
import com.trainee_management_system.cohorts.exceptions.ResourceNotFoundException;
import com.trainee_management_system.cohorts.requests.NewCohort;
import com.trainee_management_system.cohorts.resources.DetailedCohortResource;
import com.trainee_management_system.cohorts.resources.SimpleCohortResource;

import java.util.List;

public interface CohortService {

        SimpleCohortResource createCohort(NewCohort request) throws InvalidDateRangeException;

        SimpleCohortResource updateCohort(Long cohortId, NewCohort request)
                        throws ResourceNotFoundException, InvalidDateRangeException;

        List<SimpleCohortResource> getAllCohorts();

        DetailedCohortResource getCohortWithDetails(Long cohortId) throws ResourceNotFoundException;

        List<SimpleCohortResource> getAssignableCohorts();

        void deleteCohort(Long cohortId) throws ResourceNotFoundException, IllegalStateException;

        List<SimpleCohortResource> getActiveCohorts();

        List<DetailedCohortResource> getActiveCohortsWithDetails();
}