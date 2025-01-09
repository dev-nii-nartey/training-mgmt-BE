package com.grading_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class TraineeGradeHistory {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String traineeEmail;

    private double overallGradePoints;

    private int gradedQuizzesCount;

    private int gradedLabsCount;

    private int gradedPresentationsCount;
}