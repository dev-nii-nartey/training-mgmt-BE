package com.trainee_management_system.cohorts.model;

import com.trainee_management_system.cohorts.enums.CohortStatus;
import com.trainee_management_system.cohorts.resources.SimpleCohortResource;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Builder
@Table(name = "cohorts")
public class Cohort {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Cohort name is required")
    @NotBlank(message = "Cohort name cannot be empty")
    private String name;

    @ElementCollection
    @Builder.Default
    private List<Long> specializationIds = List.of();

    @NotNull(message = "Cohort start date is required")
    private LocalDate startDate;

    @NotNull(message = "Cohort end date is required")
    private LocalDate endDate;

    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CohortStatus status = CohortStatus.INACTIVE;

    @CreationTimestamp
    private Instant createAt;

    @UpdateTimestamp
    private Instant lastUpdatedAt;

    public Cohort() {
    }

    public Cohort(SimpleCohortResource createRequest) {
        this.name = createRequest.getName();
        this.startDate = createRequest.getStartDate();
        this.endDate = createRequest.getEndDate();
        this.description = createRequest.getDescription();
    }
}