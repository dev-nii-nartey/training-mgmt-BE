package com.trainee_management_system.cohorts.repository;

import com.trainee_management_system.cohorts.model.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CohortRepository extends JpaRepository<Cohort, Long> {

    @Query("SELECT c FROM Cohort c WHERE c.startDate BETWEEN :overlapStart AND :overlapEnd")
    List<Cohort> findCohortsWithinRange(@Param("overlapStart") LocalDate overlapStart,
                                        @Param("overlapEnd") LocalDate overlapEnd);

    @Query("SELECT c FROM Cohort c WHERE CURRENT_DATE BETWEEN c.startDate AND c.endDate AND c.status = 'ACTIVE'")
    List<Cohort> findActiveCohorts();

    @Query("SELECT c FROM Cohort c WHERE c.startDate >= CURRENT_DATE OR c.status = 'ACTIVE'")
    List<Cohort> findAssignableCohort();

    @Query("SELECT c FROM Cohort c WHERE c.startDate = CURRENT_DATE AND c.status = 'INACTIVE'")
    List<Cohort> findCohortStartingTodayInactive();

    @Query("SELECT c FROM Cohort c WHERE c.endDate = CURRENT_DATE AND c.status = 'ACTIVE'")
    List<Cohort> findCohortEndingTodayActive();

}