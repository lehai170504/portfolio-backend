package com.portfolio.service;

import com.portfolio.dto.request.ProjectRequest;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.utils.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse create(ProjectRequest request);

    ProjectResponse update(UUID id, ProjectRequest request);

    ProjectResponse uploadThumbnail(UUID id, MultipartFile file);

    void delete(UUID id);

    ProjectResponse getById(UUID id);

    List<ProjectResponse> getAll();

    List<ProjectResponse> getFeatured();

    PageResponse<ProjectResponse> getByStatus(ProjectStatus status, Pageable pageable);

    PageResponse<ProjectResponse> search(String keyword, Pageable pageable);

    ProjectResponse uploadImage(UUID projectId, MultipartFile file, String caption, Boolean isPrimary);

    void deleteImage(UUID projectId, UUID imageId);
}