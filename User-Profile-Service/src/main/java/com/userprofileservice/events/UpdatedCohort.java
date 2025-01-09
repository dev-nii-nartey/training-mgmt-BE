package com.userprofileservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdatedCohort {
   private Long cohortId;
    private String name;
    private List<Long> traineeIds;
    private Status status;
}
