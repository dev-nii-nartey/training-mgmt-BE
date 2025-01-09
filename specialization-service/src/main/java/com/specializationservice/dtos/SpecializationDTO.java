package com.specializationservice.dtos;

import com.specializationservice.entities.Specialization;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class SpecializationDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private List<String> prerequisites;
    private LocalDateTime createdAt;
    private SpecializationStatus status;
    private Integer traineeCount;
    private Integer trainerCount;

    public SpecializationDTO(Specialization specialization) {
        this.id = specialization.getId();
        this.name = specialization.getName();
        this.description = specialization.getDescription();
        this.prerequisites = specialization.getPrerequisites();
        this.createdAt = specialization.getCreatedAt();
        this.status = specialization.getStatus();
        this.traineeCount = specialization.getTraineeCount();
        this.trainerCount = specialization.getTrainerCount();
    }
}