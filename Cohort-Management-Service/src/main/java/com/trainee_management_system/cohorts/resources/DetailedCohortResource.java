package com.trainee_management_system.cohorts.resources;

import com.trainee_management_system.cohorts.enums.CohortStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DetailedCohortResource {
    private Long id;
    private String name;
    private CohortStatus status;
    private List<Specialization> specializations;
    private List<Trainee> trainees;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}