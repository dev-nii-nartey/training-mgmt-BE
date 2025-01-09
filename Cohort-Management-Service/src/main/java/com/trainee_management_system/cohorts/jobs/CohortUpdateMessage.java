package com.trainee_management_system.cohorts.jobs;

import com.trainee_management_system.cohorts.enums.CohortStatus;

import java.util.List;

public class CohortUpdateMessage {

    private final Long cohortId;
    private final String cohortName;
    private final CohortStatus newStatus;
    private final List<Long> trainees;

    public CohortUpdateMessage(Long cohortId, String cohortName, CohortStatus newStatus, List<Long> trainees) {
        this.cohortId = cohortId;
        this.cohortName = cohortName;
        this.newStatus = newStatus;
        this.trainees = trainees;
    }

    // Getters and Setters

    @Override
    public String toString() {
        return "CohortUpdateMessage{" +
                "cohortId=" + cohortId +
                ", cohortName='" + cohortName + '\'' +
                ", newStatus=" + newStatus +
                ", trainees=" + trainees +
                '}';
    }
}