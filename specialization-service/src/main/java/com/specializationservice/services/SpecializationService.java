package com.specializationservice.services;

import com.specializationservice.dtos.SpecializationBatchRequest;
import com.specializationservice.dtos.SpecializationDTO;
import com.specializationservice.dtos.SpecializationRequest;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface SpecializationService {
    SpecializationDTO createSpecialization(SpecializationRequest request);
    List<SpecializationDTO> getAllSpecializations();
    SpecializationDTO getSpecialization(Long id);
    SpecializationDTO updateSpecialization(Long id, SpecializationRequest request);
    void deleteSpecialization(Long id);
    void enrollTrainee(Long specializationId);
    void unEnrollTrainee(Long specializationId);
    void enrollTrainer(Long specializationId);
    void unEnrollTrainer(Long specializationId);
     Map<Long, SpecializationDTO> getSpecializationsByIds( SpecializationBatchRequest ids);

}
