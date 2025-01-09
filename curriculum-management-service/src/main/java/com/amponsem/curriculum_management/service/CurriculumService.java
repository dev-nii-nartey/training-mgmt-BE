package com.amponsem.curriculum_management.service;

import com.amponsem.curriculum_management.dto.CurriculumDetailsDTO;
import com.amponsem.curriculum_management.dto.ModuleDTO;
import com.amponsem.curriculum_management.entity.Curriculum;
import com.amponsem.curriculum_management.entity.Module;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface CurriculumService {

     Curriculum createCompleteCurriculum(CurriculumDetailsDTO curriculumDetailsDTO);

     Curriculum getCurriculum(@NotNull UUID id);

     Page<Curriculum> getAllCurricula(Pageable pageable);

     Page<Curriculum> searchCurricula(String searchTerm, Pageable pageable);

     void deleteCurriculum(@PathVariable UUID id) throws AccessDeniedException;

     Curriculum updateCompleteCurriculum(UUID id, CurriculumDetailsDTO details);
}
