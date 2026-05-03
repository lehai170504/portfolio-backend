package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_images")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "caption", length = 200)
    private String caption;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;
}
