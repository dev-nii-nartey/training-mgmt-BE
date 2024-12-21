package com.trainee_management_system.cohorts.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trainee_management_system.cohorts.enums.CohortStatus;
import com.trainee_management_system.cohorts.model.Cohort;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class NewCohort {

    private Long id;

    @NotNull(message = "Cohort name is required")
    @NotBlank(message = "Cohort name cannot be empty")
    private String name;

    private String description;

    @NotNull(message = "Cohort start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Cohort end date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "At least one specialization is required")
    @Size(min = 1, message = "At least one specialization is required")
    private List<Long> specializationIds;

    public Cohort toEntity() {
        if (this.startDate.equals(LocalDate.now())) {
            return Cohort.builder()
                    .id(this.id)
                    .name(this.name)
                    .description(this.description)
                    .startDate(this.startDate)
                    .endDate(this.endDate)
                    .status(CohortStatus.ACTIVE)
                    .specializationIds(this.specializationIds)
                    .build();
        }
        return Cohort.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .specializationIds(this.specializationIds)
                .build();
    }

}