package com.dennis.assessment_service.model;

import com.dennis.assessment_service.model.Enum.AssessmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "assessment_type", discriminatorType = DiscriminatorType.STRING)
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Assessment {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Focus area is required")
    @Size(max = 50, message = "Focus area cannot exceed 50 characters")
    private String focusArea;

    @PastOrPresent(message = "Creation date cannot be in the future")
    private LocalDateTime createdAt;

    @Lob
    private byte[] coverImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_category")
    @NotNull(message = "Assessment type is required")
    private AssessmentType assessmentType;
}
