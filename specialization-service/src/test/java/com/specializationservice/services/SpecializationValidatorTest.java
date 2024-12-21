package com.specializationservice.services;


import com.specializationservice.dtos.SpecializationRequest;
import com.specializationservice.dtos.SpecializationStatus;
import com.specializationservice.entities.Specialization;
import com.specializationservice.exceptions.BusinessValidationException;
import com.specializationservice.exceptions.ResourceAlreadyExistException;
import com.specializationservice.repositories.SpecializationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpecializationValidatorTest {

    private SpecializationRepository specializationRepository;
    private SpecializationValidator specializationValidator;

    @BeforeEach
    void setUp() {
        specializationRepository = Mockito.mock(SpecializationRepository.class);
        specializationValidator = new SpecializationValidator(specializationRepository);
    }

    @Test
    void validateCreate_whenSpecializationExistsAndIsActive_throwsException() {
        SpecializationRequest request = new SpecializationRequest();
        request.setName("Existing Specialization");

        Specialization existingSpecialization = new Specialization();
        existingSpecialization.setStatus(SpecializationStatus.ACTIVE);

        when(specializationRepository.findByNameIgnoreCase(request.getName()))
                .thenReturn(Optional.of(existingSpecialization));

        assertThrows(ResourceAlreadyExistException.class, () -> {
            specializationValidator.validateCreate(request);
        });
    }

    @Test
    void validateCreate_whenSpecializationExistsAndIsInactive_returnsSpecialization() {
        SpecializationRequest request = new SpecializationRequest();
        request.setName("Existing Specialization");

        Specialization existingSpecialization = new Specialization();
        existingSpecialization.setStatus(SpecializationStatus.INACTIVE);

        when(specializationRepository.findByNameIgnoreCase(request.getName()))
                .thenReturn(Optional.of(existingSpecialization));

        Specialization result = specializationValidator.validateCreate(request);

        assertEquals(existingSpecialization, result);
    }

    @Test
    void validateCreate_whenSpecializationDoesNotExist_returnsNull() {
        SpecializationRequest request = new SpecializationRequest();
        request.setName("New Specialization");

        when(specializationRepository.findByNameIgnoreCase(request.getName()))
                .thenReturn(Optional.empty());

        Specialization result = specializationValidator.validateCreate(request);

        assertNull(result);
    }

    @Test
    void validateUpdate_whenNameChangesAndExists_throwsException() {
        Specialization specialization = new Specialization();
        specialization.setName("Old Name");

        SpecializationRequest request = new SpecializationRequest();
        request.setName("New Name");

        when(specializationRepository.existsByNameIgnoreCase(request.getName()))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class, () -> {
            specializationValidator.validateUpdate(specialization, request);
        });
    }

    @Test
    void validateDelete_whenTraineesOrTrainersExist_throwsException() {
        Specialization specialization = new Specialization();
        specialization.setTraineeCount(1);
        specialization.setTrainerCount(0);

        assertThrows(BusinessValidationException.class, () -> {
            specializationValidator.validateDelete(specialization);
        });
    }

    @Test
    void validateEnrollment_whenSpecializationIsInactive_throwsException() {
        Specialization specialization = new Specialization();
        specialization.setStatus(SpecializationStatus.INACTIVE);

        assertThrows(BusinessValidationException.class, () -> {
            specializationValidator.validateEnrollment(specialization);
        });
    }
}