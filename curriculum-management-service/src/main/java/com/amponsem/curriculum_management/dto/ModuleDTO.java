package com.amponsem.curriculum_management.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleDTO {
    private String title;
    private String description;
    private List<String> topics = new ArrayList<>();
    private List<MultipartFile> moduleFile;

    private Long estimatedTimeMinutes = 0L;

    public Duration getEstimatedDuration() {
        return Duration.ofMinutes(estimatedTimeMinutes);
    }
}

