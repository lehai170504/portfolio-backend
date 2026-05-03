package com.portfolio.entity;

import com.portfolio.enums.SkillLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Skill extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String category;

    @Column(name = "icon_url")
    private String iconUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SkillLevel level = SkillLevel.INTERMEDIATE;

    @Column(nullable = false)
    @Builder.Default
    private Integer proficiency = 50;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_visible")
    @Builder.Default
    private Boolean isVisible = true;
}