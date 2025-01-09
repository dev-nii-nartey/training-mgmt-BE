package com.trainee_management_system.cohorts.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trainee {
    private Long id;
    private String fullName;
    private String email;
    private String contact;
    private String status;
    private String specializationName;
    private LocalDate dateCreated;
}
