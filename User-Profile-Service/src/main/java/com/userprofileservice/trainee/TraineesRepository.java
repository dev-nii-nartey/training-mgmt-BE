package com.userprofileservice.trainee;


import com.userprofileservice.events.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TraineesRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByEmailIgnoreCase(String userId);
    Page<Trainee> findAllByStatusNot(Status status, Pageable pageable); // Updated
    List<Trainee> findAllByCohortId(Long cohortId);


}
