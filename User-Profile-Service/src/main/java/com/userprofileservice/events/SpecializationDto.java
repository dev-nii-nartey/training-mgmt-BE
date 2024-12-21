package com.userprofileservice.events;



import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecializationDto {
    private Long id;
    private String name;
}