package com.specializationservice.entities;


import com.specializationservice.dtos.SpecializationDTO;
import com.specializationservice.dtos.SpecializationRequest;
import com.specializationservice.dtos.SpecializationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "specializations")
@Getter
@Setter
@NoArgsConstructor
public class Specialization implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> prerequisites;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SpecializationStatus status = SpecializationStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Helper methods for enrollment tracking
    @Column(name = "trainee_count")
    private Integer traineeCount = 0;

    @Column(name = "trainer_count")
    private Integer trainerCount = 0;

    public void incrementTraineeCount() {
        this.traineeCount = this.traineeCount + 1;
    }

    public void decrementTraineeCount() {
        if (this.traineeCount > 0) {
            this.traineeCount = this.traineeCount - 1;
        }
    }

    public void incrementTrainerCount() {
        this.trainerCount = this.trainerCount + 1;
    }

    public void decrementTrainerCount() {
        if (this.trainerCount > 0) {
            this.trainerCount = this.trainerCount - 1;
        }
    }

    public Specialization(SpecializationDTO specializationDTO) {
        this.id = specializationDTO.getId();
        this.name = specializationDTO.getName();
        this.description = specializationDTO.getDescription();
        this.prerequisites = specializationDTO.getPrerequisites();
        this.status = specializationDTO.getStatus();
        this.createdAt = specializationDTO.getCreatedAt();
    }

    public Specialization(SpecializationRequest specializationRequest){
        this.name = specializationRequest.getName();
        this.description = specializationRequest.getDescription();
        this.prerequisites = specializationRequest.getPrerequisites();
        this.createdAt = LocalDateTime.now();
    }
}