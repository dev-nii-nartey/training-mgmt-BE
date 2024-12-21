package com.amponsem.curriculum_management.repository;

import com.amponsem.curriculum_management.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface ModuleRepository extends JpaRepository<Module, UUID> {
}
