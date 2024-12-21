package com.amponsem.curriculum_management.controller;

import com.amponsem.curriculum_management.dto.CurriculumDetailsDTO;
import com.amponsem.curriculum_management.dto.ModuleDTO;
import com.amponsem.curriculum_management.entity.Curriculum;
import com.amponsem.curriculum_management.entity.Module;
import com.amponsem.curriculum_management.exception.ApiError;
import com.amponsem.curriculum_management.exception.ResourceNotFoundException;
import com.amponsem.curriculum_management.service.impl.CurriculumServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/curricula")
public class CurriculumController {

    private final CurriculumServiceImpl curriculumServiceImpl;

    public CurriculumController(CurriculumServiceImpl curriculumServiceImpl) {
        this.curriculumServiceImpl = curriculumServiceImpl;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Curriculum> createCurriculum(
            @ModelAttribute CurriculumDetailsDTO details) throws IOException {
        details.setCreatedAt(Instant.now());

        for (ModuleDTO module : details.getModules()) {
            if (module.getEstimatedTimeMinutes() == null) {
                module.setEstimatedTimeMinutes(0L);
            }
        }
        log.debug("Received request to create a curriculum : {}", details);
        return ResponseEntity.ok(curriculumServiceImpl.createCompleteCurriculum(details));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCurriculum(@PathVariable UUID id) {
        try {
            log.debug("Fetching curriculum with id: {}", id);
            Curriculum curriculum = curriculumServiceImpl.getCurriculum(id);
            return ResponseEntity.ok(curriculum);
        } catch (ResourceNotFoundException ex) {
            log.error("Curriculum not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError("Curriculum not found"));
        } catch (Exception ex) {
            log.error("Unexpected error: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("An unexpected error occurred"));
        }
    }

    @GetMapping
    public ResponseEntity<Page<Curriculum>> getAllCurricula(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(curriculumServiceImpl.getAllCurricula(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCurricula(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.debug("Searching curricula with query: {}", query);
            Page<Curriculum> curricula = curriculumServiceImpl.searchCurricula(query, PageRequest.of(page, size));
            return ResponseEntity.ok(curricula);
        } catch (Exception ex) {
            log.error("Unexpected error: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("An unexpected error occurred"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurriculum(@PathVariable UUID id) throws AccessDeniedException {
        log.debug("Deleting curriculum with id: {}", id);
        curriculumServiceImpl.deleteCurriculum(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Curriculum> updateCompleteCurriculum(
            @PathVariable UUID id,
            @ModelAttribute @Valid CurriculumDetailsDTO details) throws IOException {
        log.debug("Updating complete curriculum with id: {}", id);
        return ResponseEntity.ok(curriculumServiceImpl.updateCompleteCurriculum(id, details));
    }
}