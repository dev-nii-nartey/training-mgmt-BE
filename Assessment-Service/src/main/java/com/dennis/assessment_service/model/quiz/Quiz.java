package com.dennis.assessment_service.model.quiz;

import com.dennis.assessment_service.model.Assessment;
import com.dennis.assessment_service.model.Enum.AssessmentType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Quiz extends Assessment {
    private Integer durationInMinutes;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Question> questions;

    public Quiz() {
        super.setAssessmentType(AssessmentType.QUIZ);
    }
}

