package com.portfolio.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProfileResponse {
    private UUID id;
    private String fullName;
    private String headline;
    private String bio;
    private String avatarUrl;
    private String email;
    private String phone;
    private String location;
    private String githubUrl;
    private String linkedinUrl;
    private String portfolioUrl;
    private String resumeUrl;
    private Integer yearsOfExperience;
    private String availability;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
