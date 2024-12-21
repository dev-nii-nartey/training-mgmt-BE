package com.dennis.assessment_service.model.lab;

import com.dennis.assessment_service.model.Assessment;
import com.dennis.assessment_service.model.Enum.AssessmentType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
public class Lab extends Assessment {
    @Lob
    private byte[] file;

    @OneToMany(mappedBy = "lab", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<LabSubmission> labSubmissions;

    public Lab() {
        super.setAssessmentType(AssessmentType.LAB);
    }
}

