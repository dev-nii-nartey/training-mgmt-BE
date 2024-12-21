package com.specializationservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SpecializationRequest {
    @NotBlank(message = "Specialization name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1000)
    private String description;

    private
    List<String> prerequisites;
}
