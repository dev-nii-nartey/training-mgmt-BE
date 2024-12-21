package com.amponsem.curriculum_management.service.impl;

import com.amponsem.curriculum_management.dto.CurriculumDetailsDTO;
import com.amponsem.curriculum_management.dto.UserDTO;
import com.amponsem.curriculum_management.entity.Curriculum;
import com.amponsem.curriculum_management.entity.Module;
import com.amponsem.curriculum_management.exception.ResourceNotFoundException;
import com.amponsem.curriculum_management.repository.CurriculumRepository;
import com.amponsem.curriculum_management.repository.ModuleRepository;
import com.amponsem.curriculum_management.role.Role;
import com.amponsem.curriculum_management.service.AuthUserService;
import com.amponsem.curriculum_management.service.CurriculumService;
import com.amponsem.curriculum_management.service.FileStorageService;
import com.amponsem.curriculum_management.service.UserProfileService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@CacheConfig(cacheNames = "curricula")
@Service
@Transactional
public class CurriculumServiceImpl implements CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final ModuleRepository moduleRepository;
    private final FileStorageService fileStorageService;
    private final AuthUserService authUserService;
    private final UserProfileService userProfileService;

    public CurriculumServiceImpl(
            CurriculumRepository curriculumRepository,
            ModuleRepository moduleRepository,
            FileStorageService fileStorageService, AuthUserService authUserService, UserProfileService userProfileService) {
        this.curriculumRepository = curriculumRepository;
        this.moduleRepository = moduleRepository;
        this.fileStorageService = fileStorageService;
        this.authUserService = authUserService;
        this.userProfileService = userProfileService;
    }

    @Transactional
    public Curriculum createCompleteCurriculum(CurriculumDetailsDTO curriculumDetailsDTO){
        if (curriculumRepository.existsByTitle(curriculumDetailsDTO.getTitle())) {
            throw new IllegalArgumentException("A curriculum with the same title already exists");
        }

        Curriculum curriculum = new Curriculum();
        curriculum.setTitle(curriculumDetailsDTO.getTitle());
        curriculum.setDescription(curriculumDetailsDTO.getDescription());
        curriculum.setSpecialization(curriculumDetailsDTO.getSpecialization());
        curriculum.setLearningObjectives(curriculumDetailsDTO.getLearningObjectives());
        curriculum.setCreatedAt(curriculumDetailsDTO.getCreatedAt());
        if (curriculumDetailsDTO.getThumbnailImage() != null && !curriculumDetailsDTO.getThumbnailImage().isEmpty()) {
            String thumbnailUrl = fileStorageService.uploadFile(curriculumDetailsDTO.getThumbnailImage());
            curriculum.setThumbnailImageUrl(thumbnailUrl);
        } else {
            log.warn("Thumbnail image is null or empty. Skipping file storage.");
        }

        List<Module> modules = curriculumDetailsDTO.getModules().stream().map(moduleDTO -> {
            Module module = new Module();
            module.setTitle(moduleDTO.getTitle());
            module.setDescription(moduleDTO.getDescription());
            module.setTopics(moduleDTO.getTopics());

            module.setEstimatedTime(moduleDTO.getEstimatedDuration());

            module.setCurriculum(curriculum);

            if (moduleDTO.getModuleFile() != null && !(moduleDTO.getModuleFile().isEmpty())) {
                List<String> fileUrl = new java.util.ArrayList<>(List.of());
                for (MultipartFile file: moduleDTO.getModuleFile()){
                    fileUrl.add(fileStorageService.uploadFile(file));
                }
                module.setFileUrl(fileUrl);
            }
            return module;
        }).collect(Collectors.toList());

        moduleRepository.saveAll(modules);

        curriculum.setModules(modules);
        Curriculum createdCurriculum =curriculumRepository.save(curriculum);
        UserDTO userDTO = userProfileService.getUserDetails(createdCurriculum.getCreatedBy());
        curriculum.setCreatedBy(userDTO.getUsername());
        return createdCurriculum;
    }

    public Curriculum getCurriculum(@NotNull UUID id) {
        return curriculumRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Curriculum not found"));
    }

    public Page<Curriculum> getAllCurricula(Pageable pageable) {
        return curriculumRepository.findAll(pageable);
    }

    public Page<Curriculum> searchCurricula(String searchTerm, Pageable pageable) {
        return curriculumRepository.findByTitleContainingIgnoreCase(searchTerm, pageable);
    }

    public void deleteCurriculum(UUID id) throws AccessDeniedException {

        if(!authUserService.getUserRole().equals(Role.ADMIN.name())){
            throw new AccessDeniedException("Curriculum can only be deleted by admin.");
        }

        Curriculum curriculum = getCurriculum(id);
        curriculumRepository.delete(curriculum);
    }

    @Transactional
    public Curriculum updateCompleteCurriculum(UUID id, CurriculumDetailsDTO details) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum not found"));

        // Set basic details
        curriculum.setTitle(details.getTitle());
        curriculum.setDescription(details.getDescription());
        curriculum.setSpecialization(details.getSpecialization());
        curriculum.setLearningObjectives(details.getLearningObjectives());

        if (details.getThumbnailImage() != null && !details.getThumbnailImage().isEmpty()) {
            try {
                String thumbnailUrl = fileStorageService.uploadFile(details.getThumbnailImage());
                curriculum.setThumbnailImageUrl(thumbnailUrl);
            } catch (Exception e) {
                log.info("Error uploading thumbnail image. Try again later.");
                throw new RuntimeException("Thumbnail image upload failed. Try again later. ");
            }
        }

        List<Module> newModules = details.getModules().stream().map(moduleDTO -> {
            Module module = new Module();
            module.setTitle(moduleDTO.getTitle());
            module.setDescription(moduleDTO.getDescription());
            module.setTopics(moduleDTO.getTopics());
            module.setEstimatedTime(moduleDTO.getEstimatedDuration());
            module.setCurriculum(curriculum);
            if (moduleDTO.getModuleFile() != null && !moduleDTO.getModuleFile().isEmpty()) {
                try {
                    List<String> fileUrl = new java.util.ArrayList<>(List.of());
                    for (MultipartFile file: moduleDTO.getModuleFile()){
                        fileUrl.add(fileStorageService.uploadFile(file));
                    }
                    module.setFileUrl(fileUrl);
                } catch (Exception e) {
                    log.info("Error uploading module file: " + e.getMessage());
                    throw new RuntimeException("Module file upload failed: " + e.getMessage());
                }
            }
            return module;
        }).collect(Collectors.toList());
        curriculum.getModules().addAll(newModules);
        moduleRepository.saveAll(newModules);
        Curriculum createdCurriculum = curriculumRepository.save(curriculum);
        UserDTO userDTO = userProfileService.getUserDetails(createdCurriculum.getCreatedBy());
        curriculum.setCreatedBy(userDTO.getUsername());
        return createdCurriculum;
    }

}