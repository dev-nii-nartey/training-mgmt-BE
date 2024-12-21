package com.amponsem.curriculum_management.repository;

import com.amponsem.curriculum_management.entity.Curriculum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CurriculumRepository extends JpaRepository<Curriculum, UUID> {
    boolean existsByTitle(String title);

    Page<Curriculum> findByTitleContainingIgnoreCase(String searchTerm, Pageable pageable);
}


