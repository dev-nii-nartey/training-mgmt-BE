package com.dennis.assessment_service.dto.quiz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AnswerDTO {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Answer text is required")
    private String text;

    @JsonIgnore
    private QuestionDTO question;

}
