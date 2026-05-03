package com.portfolio.dto.request;
import com.portfolio.enums.SkillLevel;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String category;

    private String iconUrl;

    private SkillLevel level = SkillLevel.INTERMEDIATE;

    @Min(0) @Max(100)
    private Integer proficiency = 50;

    private Integer displayOrder = 0;
    private Boolean isVisible = true;

}
