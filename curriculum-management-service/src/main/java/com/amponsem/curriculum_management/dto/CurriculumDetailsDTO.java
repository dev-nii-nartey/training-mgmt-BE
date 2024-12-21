package com.amponsem.curriculum_management.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class CurriculumDetailsDTO {
    private String title;
    private String description;
    private String specialization;
    private List<String> learningObjectives = new ArrayList<>();
    private MultipartFile thumbnailImage;
    private List<ModuleDTO> modules = new ArrayList<>();
    private Instant createdAt;
    private String creatorUserName;
}
