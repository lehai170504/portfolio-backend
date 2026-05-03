package com.portfolio.dto.request;
import com.portfolio.enums.ExperienceType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExperienceRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100)
    private String title;

    @NotBlank(message = "Organization is required")
    @Size(max = 150)
    private String organization;

    private String description;

    @Size(max = 100)
    private String location;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isCurrent = false;

    private ExperienceType type = ExperienceType.WORK;

    private Integer displayOrder = 0;
}
