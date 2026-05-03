package com.portfolio.dto.response;

import com.portfolio.enums.ProjectStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ProjectResponse {
    private UUID id;
    private String title;
    private String description;
    private String shortDescription;
    private String thumbnailUrl;
    private String demoUrl;
    private String githubUrl;
    private List<String> techStack;
    private ProjectStatus status;
    private Integer displayOrder;
    private Boolean isFeatured;
    private Long viewCount;
    private List<ProjectImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
