package com.trainee_management_system.cohorts.service;

import com.trainee_management_system.cohorts.exceptions.ResourceNotFoundException;
import com.trainee_management_system.cohorts.model.Cohort;
import com.trainee_management_system.cohorts.repository.CohortRepository;
import com.trainee_management_system.cohorts.requests.NewCohort;
import com.trainee_management_system.cohorts.resources.DetailedCohortResource;
import com.trainee_management_system.cohorts.resources.SimpleCohortResource;
import com.trainee_management_system.cohorts.resources.Specialization;
import com.trainee_management_system.cohorts.resources.Trainee;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class CohortServiceImpl implements CohortService {

    private final CohortRepository cohortRepository;
    private final SpecializationService specializationService;
    private final UserProfileService userProfileService;
    private final CohortValidationService cohortValidationService;

    public CohortServiceImpl(CohortRepository cohortRepository,
                             SpecializationService specializationService,
                             UserProfileService userProfileService,
                             CohortValidationService cohortValidationService) {
        this.cohortRepository = cohortRepository;
        this.specializationService = specializationService;
        this.userProfileService = userProfileService;
        this.cohortValidationService = cohortValidationService;
    }

    @Override
    public SimpleCohortResource createCohort(NewCohort request) throws IllegalArgumentException {
        cohortValidationService.validateCreate(request);
        cohortValidationService.validateNonOverlappingStartDate(request.getEndDate());
        Cohort cohort = cohortRepository.save(request.toEntity());
        return SimpleCohortResource.fromEntity(cohort, 0);
    }

    @Override
    public SimpleCohortResource updateCohort(Long cohortId, NewCohort request) throws IllegalArgumentException{
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found"));

        cohortValidationService.validateUpdate(cohort,request);
        cohortValidationService.validateNonOverlappingStartDate(request.getStartDate());

        cohort.setName(request.getName());
        cohort.setSpecializationIds(request.getSpecializationIds());
        cohort.setStartDate(request.getStartDate());
        cohort.setEndDate(request.getEndDate());

        Cohort updatedCohort = cohortRepository.save(cohort);
        int traineesCount = userProfileService.getTraineesByCohortId(updatedCohort.getId()).size();

        return SimpleCohortResource.fromEntity(updatedCohort, traineesCount);
    }

    @Override
    public List<SimpleCohortResource> getAllCohorts() {
        List<Cohort> cohorts = cohortRepository.findAll();
        return cohorts.stream()
                .map(cohort -> {
                    int traineesCount = userProfileService.getTraineesByCohortId(cohort.getId()).size();
                    return SimpleCohortResource.fromEntity(cohort, traineesCount);
                })
                .toList();
    }

    @Override
    public DetailedCohortResource getCohortWithDetails(Long cohortId) throws ResourceNotFoundException {
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found"));

        List<Specialization> specializations = specializationService.getSpecializationsByIds(cohort.getSpecializationIds());
        List<Trainee> trainees = userProfileService.getTraineesByCohortId(cohort.getId());

        return mapToDetails(cohort, specializations, trainees);
    }

    @Override
    public void deleteCohort(Long cohortId) throws ResourceNotFoundException, IllegalStateException {
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found"));

        List<Trainee> trainees = userProfileService.getTraineesByCohortId(cohort.getId());

        if (!trainees.isEmpty()) {
            throw new IllegalStateException("Cannot delete cohort with trainees associated");
        }

        cohortRepository.deleteById(cohortId);
    }

    @Override
    public List<SimpleCohortResource> getAssignableCohorts() {
        List<Cohort> activeCohorts = cohortRepository.findAssignableCohort();
        return activeCohorts.stream()
                .map(cohort -> {
                    int traineesCount = userProfileService.getTraineesByCohortId(cohort.getId()).size();
                    return SimpleCohortResource.fromEntity(cohort, traineesCount);
                })
                .toList();
    }

    @Override
    public List<SimpleCohortResource> getActiveCohorts() {
        List<Cohort> activeCohorts = cohortRepository.findActiveCohorts();
        return activeCohorts.stream()
                .map(cohort -> {
                    int traineesCount = userProfileService.getTraineesByCohortId(cohort.getId()).size();
                    return SimpleCohortResource.fromEntity(cohort, traineesCount);
                })
                .toList();
    }

    @Override
    public List<DetailedCohortResource> getActiveCohortsWithDetails() {
        List<Cohort> activeCohorts = cohortRepository.findActiveCohorts();
        List<DetailedCohortResource> resources = new ArrayList<>();
        for (Cohort activeCohort : activeCohorts) {
            resources.add(getCohortWithDetails(activeCohort.getId()));
        }
        return resources;
    }

    private DetailedCohortResource mapToDetails(Cohort cohort, List<Specialization> specializations,
                                                List<Trainee> trainees) {
        return DetailedCohortResource.builder()
                .id(cohort.getId())
                .name(cohort.getName())
                .status(cohort.getStatus())
                .specializations(specializations)
                .trainees(trainees)
                .startDate(cohort.getStartDate())
                .endDate(cohort.getEndDate())
                .description(cohort.getDescription())
                .build();
    }
}
