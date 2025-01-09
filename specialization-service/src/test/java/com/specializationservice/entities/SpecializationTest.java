package com.specializationservice.entities;

import com.specializationservice.dtos.SpecializationDTO;
import com.specializationservice.dtos.SpecializationRequest;
import com.specializationservice.dtos.SpecializationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SpecializationTest {

    private Specialization specialization;
    private SpecializationRequest createSpecializationRequest;

    @BeforeEach
    public void setUp() {
        SpecializationDTO specializationDTO = new SpecializationDTO();
        specializationDTO.setId(1L);
        specializationDTO.setName("Java Programming");
        specializationDTO.setDescription("Learn Java from scratch");
        specializationDTO.setPrerequisites(List.of("Basic Programming"));
        specializationDTO.setStatus(SpecializationStatus.ACTIVE);
        specializationDTO.setCreatedAt(LocalDateTime.now());

        createSpecializationRequest = new SpecializationRequest();
        createSpecializationRequest.setName("Python Programming");
        createSpecializationRequest.setDescription("Learn Python from scratch");
        createSpecializationRequest.setPrerequisites(List.of("Basic Programming"));

        specialization = new Specialization(specializationDTO);
    }

    @Test
    public void testSpecializationConstructorWithDTO() {
        assertEquals(1L, specialization.getId());
        assertEquals("Java Programming", specialization.getName());
        assertEquals("Learn Java from scratch", specialization.getDescription());
        assertEquals(List.of("Basic Programming"), specialization.getPrerequisites());
        assertEquals(SpecializationStatus.ACTIVE, specialization.getStatus());
        assertNotNull(specialization.getCreatedAt());
    }

    @Test
    public void testSpecializationConstructorWithCreateRequest() {
        Specialization newSpecialization = new Specialization(createSpecializationRequest);
        assertEquals("Python Programming", newSpecialization.getName());
        assertEquals("Learn Python from scratch", newSpecialization.getDescription());
        assertEquals(List.of("Basic Programming"), newSpecialization.getPrerequisites());
        assertNotNull(newSpecialization.getCreatedAt());
    }

    @Test
    public void testIncrementTraineeCount() {
        specialization.incrementTraineeCount();
        assertEquals(1, specialization.getTraineeCount());
    }

    @Test
    public void testDecrementTraineeCount() {
        specialization.incrementTraineeCount();
        specialization.decrementTraineeCount();
        assertEquals(0, specialization.getTraineeCount());
    }

    @Test
    public void testIncrementTrainerCount() {
        specialization.incrementTrainerCount();
        assertEquals(1, specialization.getTrainerCount());
    }

    @Test
    public void testDecrementTrainerCount() {
        specialization.incrementTrainerCount();
        specialization.decrementTrainerCount();
        assertEquals(0, specialization.getTrainerCount());
    }
}