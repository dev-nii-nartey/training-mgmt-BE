package com.specializationservice.services;

import com.specializationservice.dtos.*;
import com.specializationservice.entities.Specialization;
import com.specializationservice.exceptions.ResourceNotFoundException;
import com.specializationservice.repositories.SpecializationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@CacheConfig(cacheNames = {"specializations"})
public class SpecializationServiceImpl implements SpecializationService {
    private final SpecializationRepository specializationRepository;
    private final SpecializationValidator validator;

    public SpecializationServiceImpl(
            SpecializationRepository specializationRepository,
            SpecializationValidator validator
    ) {
        this.specializationRepository = specializationRepository;
        this.validator = validator;
    }

    @Override
    @CacheEvict(cacheNames = {"specializations"}, allEntries = true)
    public SpecializationDTO createSpecialization(SpecializationRequest request) {
        Specialization existingSpecialization = validator.validateCreate(request);

        if (existingSpecialization != null) {
            existingSpecialization.setStatus(SpecializationStatus.ACTIVE);
            existingSpecialization.setDescription(request.getDescription());
            existingSpecialization.setPrerequisites(request.getPrerequisites());
            return new SpecializationDTO(specializationRepository.save(existingSpecialization));
        }

        Specialization newSpecialization = new Specialization(request);
        return new SpecializationDTO(specializationRepository.save(newSpecialization));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecializationDTO> getAllSpecializations() {
        return specializationRepository.findAll().stream()
                .map(SpecializationDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<Long, SpecializationDTO> getSpecializationsByIds(SpecializationBatchRequest specializationIds) {
        return specializationRepository.findByIdIn(specializationIds.getIds()).stream()
                .map(SpecializationDTO::new)
                .collect(Collectors.toMap(
                        SpecializationDTO::getId,
                        Function.identity()
                ));
    }


    @Override
    @Cacheable(
            cacheNames = "specializations",
            key = "#id",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public SpecializationDTO getSpecialization(Long id) {
        return new SpecializationDTO(findSpecializationById(id));
    }

    @Override
    @CacheEvict(cacheNames = {"specializations"}, allEntries = true)
    public SpecializationDTO updateSpecialization(Long id, SpecializationRequest request) {
        Specialization specialization = findSpecializationById(id);
        validator.validateUpdate(specialization, request);

        specialization.setName(request.getName());
        specialization.setDescription(request.getDescription());
        specialization.setPrerequisites(request.getPrerequisites());

        return new SpecializationDTO(specializationRepository.save(specialization));
    }

    @Override
    @CacheEvict(cacheNames = {"specializations"}, allEntries = true)
    public void deleteSpecialization(Long id) {
        Specialization specialization = findSpecializationById(id);
        validator.validateDelete(specialization);
        specializationRepository.delete(specialization);
    }

    private Specialization findSpecializationById(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with id: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"specializations"}, allEntries = true)
    public void enrollTrainee(Long specializationId) {
        Specialization specialization = findSpecializationById(specializationId);
        validator.validateEnrollment(specialization);
        specialization.incrementTraineeCount();
        specializationRepository.save(specialization);


    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"specializations"}, allEntries = true)
    public void unEnrollTrainee(Long specializationId) {
        Specialization specialization = findSpecializationById(specializationId);
        specialization.decrementTraineeCount();
        specializationRepository.save(specialization);



    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"specializations"}, allEntries = true)
    public void enrollTrainer(Long specializationId) {
        Specialization specialization = findSpecializationById(specializationId);
        validator.validateEnrollment(specialization);
        specialization.incrementTrainerCount();
        specializationRepository.save(specialization);



    }

    @Override
    @Transactional
   @CacheEvict(cacheNames = {"specializations"}, allEntries = true)
    public void unEnrollTrainer(Long specializationId) {
        Specialization specialization = findSpecializationById(specializationId);
        specialization.decrementTrainerCount();
        specializationRepository.save(specialization);


    }
}