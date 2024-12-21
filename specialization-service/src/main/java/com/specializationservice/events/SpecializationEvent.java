package com.specializationservice.events;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpecializationEvent {
    private Long specializationId;
    private EnrollmentEventType eventType;
    private UserType userType;

}
