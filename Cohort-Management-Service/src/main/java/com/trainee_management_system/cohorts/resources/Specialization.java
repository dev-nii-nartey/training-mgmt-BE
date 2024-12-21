package com.trainee_management_system.cohorts.resources;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Specialization {
    private Long id;
    private String name;
    private String description;

    public Specialization() {
    }
}
