package com.dennis.assessment_service.dto.quiz;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionDTO {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Question text is required")
    @Size(max = 500, message = "Question text cannot exceed 500 characters")
    private String text;

    @Size(min = 1, message = "A question must have at least one answer")
    private List<AnswerDTO> answersDTO;

}
