package com.portfolio.dto.request;

import com.portfolio.enums.ProjectStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
public class ProjectRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    private String description;

    @Size(max = 300, message = "Short description must not exceed 300 characters")
    private String shortDescription;

    @URL(message = "Invalid demo URL format")
    private String demoUrl;

    @URL(message = "Invalid GitHub URL format")
    private String githubUrl;

    private List<String> techStack;

    private ProjectStatus status = ProjectStatus.ACTIVE;

    private Integer displayOrder = 0;

    private Boolean isFeatured = false;
}