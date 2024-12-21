package com.specializationservice.services;


import com.specializationservice.dtos.SpecializationRequest;
import com.specializationservice.dtos.SpecializationStatus;
import com.specializationservice.entities.Specialization;
import com.specializationservice.exceptions.BusinessValidationException;
import com.specializationservice.exceptions.ResourceAlreadyExistException;
import com.specializationservice.exceptions.ResourceNotFoundException;
import com.specializationservice.repositories.SpecializationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class SpecializationValidator {
    private final SpecializationRepository specializationRepository;

    public SpecializationValidator(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public Specialization validateCreate(SpecializationRequest request) {
        Optional<Specialization> existingSpecialization = specializationRepository.findByNameIgnoreCase(request.getName());
        if (existingSpecialization.isPresent()) {
            Specialization specialization = existingSpecialization.get();
            if (specialization.getStatus() == SpecializationStatus.INACTIVE) {
                return specialization; // Return the inactive specialization for reactivation
            } else {
                throw new ResourceAlreadyExistException("Specialization with name '" + request.getName() + "' already exists");
            }
        }
        return null; // Return null if no existing specialization found
    }


    public void validateUpdate(Specialization specialization, SpecializationRequest request) {
        if (specialization.getStatus() == SpecializationStatus.INACTIVE ) {
            throw new BusinessValidationException("Resource doesn't exist");
        }
        if (!specialization.getName().equalsIgnoreCase(request.getName()) &&
                specializationRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistException("Specialization with this name already exists");
        }
    }


    public void validateDelete(Specialization specialization) {
        if (specialization.getTraineeCount() > 0 ) {
            throw new BusinessValidationException("Cannot delete specialization with Trainees assigned to it");
        }
        if (specialization.getStatus() == SpecializationStatus.INACTIVE ) {
            throw new ResourceNotFoundException("Resource doesn't exist");
        }
    }

    public void validateEnrollment(Specialization specialization) {
        if (specialization.getStatus() == SpecializationStatus.INACTIVE) {
            throw new BusinessValidationException("Cannot enroll in inactive specialization");
        }
    }
}
