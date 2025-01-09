package com.trainee_management_system.cohorts.controllers;

import com.trainee_management_system.cohorts.requests.NewCohort;
import com.trainee_management_system.cohorts.resources.SimpleCohortResource;
import com.trainee_management_system.cohorts.resources.DetailedCohortResource;
import com.trainee_management_system.cohorts.service.CohortService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/cohorts")
public class CohortsManagementController {

    private final CohortService cohortService;

    public CohortsManagementController(CohortService cohortService) {
        this.cohortService = cohortService;
    }

    @GetMapping
    public ResponseEntity<List<SimpleCohortResource>> getCohorts() {
        return ResponseEntity.ok(cohortService.getAllCohorts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedCohortResource> getCohortDetails(@PathVariable Long id) {
        return ResponseEntity.ok(cohortService.getCohortWithDetails(id));
    }

    @PostMapping
    public ResponseEntity<SimpleCohortResource> createCohort(@Valid @RequestBody NewCohort request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cohortService.createCohort(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SimpleCohortResource> updateCohort(
            @PathVariable Long id,
            @Valid @RequestBody NewCohort request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(cohortService.updateCohort(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCohort(@PathVariable Long id) {
        cohortService.deleteCohort(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assignable")
    public ResponseEntity<List<SimpleCohortResource>> getAssignableCohorts() {
        return ResponseEntity.ok(cohortService.getAssignableCohorts());
    }

    @GetMapping("/active")
    public ResponseEntity<List<SimpleCohortResource>> getActiveCohorts() {
        return ResponseEntity.ok(cohortService.getActiveCohorts());
    }

    @GetMapping("/progress")
    public ResponseEntity<List<DetailedCohortResource>> getActiveCohortsWithDetails() {
        return ResponseEntity.ok(cohortService.getActiveCohortsWithDetails());
    }

    @GetMapping("/test")
    public String returnString() {
        return "Hello, this is a test response! for services";
    }
}
