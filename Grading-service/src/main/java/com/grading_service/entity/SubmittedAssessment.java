package com.grading_service.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private UUID assessmentId;
    private String title;
    private AssessmentType type;
    private String traineeEmail;
    private Long labByWeek;
    private LocalDateTime dateSubmitted = LocalDateTime.now();
    private LocalDate dateCreated = LocalDate.now();

    @ElementCollection(fetch = FetchType.EAGER)  // Change to EAGER temporarily
    private List<String> url = new ArrayList<>();
    private double totalMarks =0.0;
    private String letterGrade;
    private boolean graded;
    @Lob
    private byte[] file;
}