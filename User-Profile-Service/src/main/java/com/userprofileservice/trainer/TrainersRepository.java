package com.userprofileservice.trainer;


import com.userprofileservice.events.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TrainersRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByEmailIgnoreCase(String email);
    Page<Trainer> findAllByStatus(Status status, Pageable pageable);

}
