package com.dennis.assessment_service.model;

import com.dennis.assessment_service.model.Enum.AssessmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
public class Presentation extends Assessment {

    @Lob
    private byte[] file;

    public Presentation() {
        super.setAssessmentType(AssessmentType.PRESENTATION);
    }
}

