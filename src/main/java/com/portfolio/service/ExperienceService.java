package com.portfolio.service;

import com.portfolio.dto.request.ExperienceRequest;
import com.portfolio.dto.response.ExperienceResponse;
import com.portfolio.enums.ExperienceType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ExperienceService {
    ExperienceResponse create(ExperienceRequest request);
    ExperienceResponse update(UUID id, ExperienceRequest request);
    ExperienceResponse uploadLogo(UUID id, MultipartFile file);
    void delete(UUID id);
    List<ExperienceResponse> getAll();
    List<ExperienceResponse> getByType(ExperienceType type);
}