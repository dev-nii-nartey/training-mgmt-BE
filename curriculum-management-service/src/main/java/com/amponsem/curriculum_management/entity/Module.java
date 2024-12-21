package com.amponsem.curriculum_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Module {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @ElementCollection
    private List<String> topics;

    @ElementCollection
    private List<String> fileUrl;

    private Duration estimatedTime;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;
}