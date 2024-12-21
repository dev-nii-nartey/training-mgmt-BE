package com.specializationservice.repositories;

import com.specializationservice.dtos.SpecializationStatus;
import com.specializationservice.entities.Specialization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    Optional<Specialization> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    List<Specialization> findByIdIn(Collection<Long> ids);
}
