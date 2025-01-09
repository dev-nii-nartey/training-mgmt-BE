package com.specializationservice.controllers;


import com.specializationservice.dtos.SpecializationDTO;
import com.specializationservice.dtos.SpecializationRequest;
import com.specializationservice.services.SpecializationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpecializationControllerTest {

    @Mock
    private SpecializationService specializationService;

    @InjectMocks
    private SpecializationController specializationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSpecialization() {
        // Arrange
        SpecializationRequest request = new SpecializationRequest();
        SpecializationDTO specializationDTO = new SpecializationDTO();

        when(specializationService.createSpecialization(request)).thenReturn(specializationDTO);

        // Act
        SpecializationDTO result = specializationController.createSpecialization(request);

        // Assert
        assertNotNull(result);
        assertEquals(specializationDTO, result);
        verify(specializationService, times(1)).createSpecialization(request);
    }



    @Test
    void testGetSpecialization() {
        // Arrange
        Long id = 1L;
        SpecializationDTO specializationDTO = new SpecializationDTO();
        when(specializationService.getSpecialization(id)).thenReturn(specializationDTO);

        // Act
        SpecializationDTO result = specializationController.getSpecialization(id);

        // Assert
        assertNotNull(result);
        assertEquals(specializationDTO, result);
        verify(specializationService, times(1)).getSpecialization(id);
    }

    @Test
    void testGetSpecialization_NotFound() {
        // Arrange
        Long id = 1L;
        when(specializationService.getSpecialization(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> specializationController.getSpecialization(id));
        verify(specializationService, times(1)).getSpecialization(id);
    }

    @Test
    void testUpdateSpecialization() {
        // Arrange
        Long id = 1L;
        SpecializationRequest request = new SpecializationRequest();
        SpecializationDTO specializationDTO = new SpecializationDTO();
        when(specializationService.updateSpecialization(id, request)).thenReturn(specializationDTO);

        // Act
        SpecializationDTO result = specializationController.updateSpecialization(id, request);

        // Assert
        assertNotNull(result);
        assertEquals(specializationDTO, result);
        verify(specializationService, times(1)).updateSpecialization(id, request);
    }

    @Test
    void testDeleteSpecialization() {
        // Arrange
        Long id = 1L;

        // Act
        specializationController.deleteSpecialization(id);

        // Assert
        verify(specializationService, times(1)).deleteSpecialization(id);
    }
}
