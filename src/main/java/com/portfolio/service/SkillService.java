package com.portfolio.service;

import com.portfolio.dto.request.SkillRequest;
import com.portfolio.dto.response.SkillResponse;

import java.util.List;
import java.util.UUID;

public interface SkillService {
    SkillResponse create(SkillRequest request);
    SkillResponse update(UUID id, SkillRequest request);
    void delete(UUID id);
    List<SkillResponse> getAll();
    List<SkillResponse> getByCategory(String category);
}