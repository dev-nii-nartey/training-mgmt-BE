package com.userprofileservice.events;

import com.userprofileservice.trainee.Trainee;
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

    public CohortResponseDto(Trainee trainee) {
        this.id = trainee.getId();
        this.fullName = trainee.getFirstName();
        this.email = trainee.getEmail();
        this.contact = trainee.getPhoneNumber();
        this.status = trainee.getStatus();
        this.dateCreated = trainee.getDateCreated();
    }
}
