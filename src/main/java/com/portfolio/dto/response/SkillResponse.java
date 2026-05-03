package com.portfolio.dto.response;

import com.portfolio.enums.SkillLevel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SkillResponse {
    private UUID id;
    private String name;
    private String category;
    private String iconUrl;
    private SkillLevel level;
    private Integer proficiency;
    private Integer displayOrder;
    private Boolean isVisible;
    private LocalDateTime createdAt;
}
