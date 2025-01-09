package com.dennis.assessment_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CohortResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String contact;
    private Status status;
    private String specializationName;
    private LocalDateTime dateCreated;
}