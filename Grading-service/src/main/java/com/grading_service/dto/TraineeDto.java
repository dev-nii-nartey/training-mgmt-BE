package com.grading_service.dto;


import java.util.List;

public record TraineeDto (
     String firstName,
     String LastName,
     String specialization,
     double grade,
     String email,
     List<String> url
)
{
}