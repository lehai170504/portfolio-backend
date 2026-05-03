package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "project_views")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Column(name = "project_id", nullable = false)
    private java.util.UUID projectId;

    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;

    @Column(nullable = false)
    @Builder.Default
    private Long count = 1L;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;
}
