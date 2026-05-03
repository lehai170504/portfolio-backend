package com.portfolio.dto.response;

import com.portfolio.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminProfileResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private UserRole role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}