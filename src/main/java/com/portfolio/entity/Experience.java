package com.portfolio.entity;

import com.portfolio.enums.ExperienceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "experiences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Experience extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 150)
    private String organization;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current")
    @Builder.Default
    private Boolean isCurrent = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ExperienceType type = ExperienceType.WORK;

    @Column(name = "company_logo_url")
    private String companyLogoUrl;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}