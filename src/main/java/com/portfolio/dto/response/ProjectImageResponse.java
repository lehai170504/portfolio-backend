package com.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectImageResponse {
    private UUID id;
    private String imageUrl;
    private String caption;
    private Integer displayOrder;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}
