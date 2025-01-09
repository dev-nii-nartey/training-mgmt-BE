package com.specializationservice.controllers;


import com.specializationservice.dtos.SpecializationBatchRequest;
import com.specializationservice.dtos.SpecializationDTO;
import com.specializationservice.dtos.SpecializationRequest;
import com.specializationservice.services.SpecializationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/v1/specializations")
@Validated
public class SpecializationController {
    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpecializationDTO createSpecialization(@Valid @RequestBody SpecializationRequest request) {
        log.info("selkkskss {}", request);
        return specializationService.createSpecialization(request);
    }

    @GetMapping
    public List<SpecializationDTO> getAllSpecializations (){
        return specializationService.getAllSpecializations();
    }

    @GetMapping("/{id}")
    public SpecializationDTO getSpecialization(@PathVariable Long id) {
        return specializationService.getSpecialization(id);
    }

    @PostMapping("/batch")
    public Map<Long, SpecializationDTO> getSpecializationsByIds(@RequestBody SpecializationBatchRequest ids) {
        return specializationService.getSpecializationsByIds(ids);
    }

    @PutMapping("/{id}")
    public SpecializationDTO updateSpecialization(
            @PathVariable Long id,
            @Valid @RequestBody SpecializationRequest request) {
        return specializationService.updateSpecialization(id, request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteSpecialization(id);
    }

    @GetMapping("/test")
    public String returnString() {
        String test = "Hello, this is a test response! for services";
        return test;
    }

}