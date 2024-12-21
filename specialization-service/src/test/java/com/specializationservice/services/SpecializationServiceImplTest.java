package com.specializationservice.services;

import com.specializationservice.dtos.SpecializationDTO;
import com.specializationservice.dtos.SpecializationRequest;
import com.specializationservice.dtos.SpecializationStatus;
import com.specializationservice.entities.Specialization;
import com.specializationservice.exceptions.ResourceNotFoundException;
import com.specializationservice.repositories.SpecializationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecializationServiceImplTest {

    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private SpecializationValidator validator;


    private SpecializationServiceImpl specializationService;

    @BeforeEach
    void setUp() {
        specializationService = new SpecializationServiceImpl(specializationRepository, validator);
    }

    @Test
    void createSpecialization_NewSpecialization_Success() {
        // Arrange
        SpecializationRequest request = new SpecializationRequest();
        request.setName("Java Development");
        request.setDescription("Java programming course");
        request.setPrerequisites(List.of("Basic programming knowledge"));

        Specialization newSpecialization = new Specialization(request);
        when(validator.validateCreate(request)).thenReturn(null);
        when(specializationRepository.save(any(Specialization.class))).thenReturn(newSpecialization);

        // Act
        SpecializationDTO result = specializationService.createSpecialization(request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getDescription(), result.getDescription());
        verify(specializationRepository).save(any(Specialization.class));
    }

    @Test
    void createSpecialization_ReactivateExisting_Success() {
        // Arrange
        SpecializationRequest request = new SpecializationRequest();
        request.setName("Java Development");
        request.setDescription("Updated description");
        request.setPrerequisites(List.of("Updated prerequisites"));

        Specialization existingSpecialization = new Specialization(request);
        existingSpecialization.setStatus(SpecializationStatus.INACTIVE);

        when(validator.validateCreate(request)).thenReturn(existingSpecialization);
        when(specializationRepository.save(any(Specialization.class))).thenReturn(existingSpecialization);

        // Act
        SpecializationDTO result = specializationService.createSpecialization(request);

        // Assert
        assertNotNull(result);
        assertEquals(SpecializationStatus.ACTIVE, existingSpecialization.getStatus());
        assertEquals(request.getDescription(), result.getDescription());
        verify(specializationRepository).save(existingSpecialization);
    }


    @Test
    void getSpecialization_Success() {
        // Arrange
        Long id = 1L;
        Specialization specialization = new Specialization();
        specialization.setId(id);
        specialization.setName("Java Development");

        when(specializationRepository.findById(id)).thenReturn(Optional.of(specialization));

        // Act
        SpecializationDTO result = specializationService.getSpecialization(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Java Development", result.getName());
    }

    @Test
    void getSpecialization_NotFound() {
        // Arrange
        Long id = 1L;
        when(specializationRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> specializationService.getSpecialization(id));
    }

    @Test
    void updateSpecialization_Success() {
        // Arrange
        Long id = 1L;
        SpecializationRequest request = new SpecializationRequest();
        request.setName("Updated Java");
        request.setDescription("Updated description");
        request.setPrerequisites(List.of("Updated prerequisites"));

        Specialization existingSpecialization = new Specialization();
        existingSpecialization.setId(id);

        when(specializationRepository.findById(id)).thenReturn(Optional.of(existingSpecialization));
        when(specializationRepository.save(any(Specialization.class))).thenReturn(existingSpecialization);

        // Act
        SpecializationDTO result = specializationService.updateSpecialization(id, request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getName(), result.getName());
        verify(validator).validateUpdate(existingSpecialization, request);
        verify(specializationRepository).save(existingSpecialization);
    }


}