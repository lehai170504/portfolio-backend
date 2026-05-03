package com.portfolio.dto.response;

import com.portfolio.enums.ExperienceType;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExperienceResponse {
    private UUID id;
    private String title;
    private String organization;
    private String description;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private ExperienceType type;
    private String companyLogoUrl;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}
