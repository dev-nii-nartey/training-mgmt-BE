package com.trainee_management_system.cohorts.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trainee_management_system.cohorts.enums.CohortStatus;
import com.trainee_management_system.cohorts.model.Cohort;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCohortResource {
    private Long id;

    @NotBlank(message = "Cohort name is required")
    private String name;

    private String description;

    @NotNull(message = "Cohort start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Cohort end date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private CohortStatus status;
    private int traineesEnrolled;

    public static SimpleCohortResource fromEntity(Cohort cohort, int traineesCount) {
        return SimpleCohortResource.builder()
                .id(cohort.getId())
                .name(cohort.getName())
                .description(cohort.getDescription())
                .startDate(cohort.getStartDate())
                .endDate(cohort.getEndDate())
                .status(cohort.getStatus())
                .traineesEnrolled(traineesCount)
                .build();
    }
}
