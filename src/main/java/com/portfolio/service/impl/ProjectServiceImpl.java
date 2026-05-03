package com.portfolio.service.impl;

import com.portfolio.dto.request.ProjectRequest;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.entity.Project;
import com.portfolio.entity.ProjectImage;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.exception.AppException;
import com.portfolio.mapper.ProjectImageMapper;
import com.portfolio.mapper.ProjectMapper;
import com.portfolio.repository.ProjectImageRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.service.CloudinaryService;
import com.portfolio.service.ProjectService;
import com.portfolio.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectImageRepository projectImageRepository;
    private final ProjectMapper     projectMapper;
    private final ProjectImageMapper projectImageMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "featuredProjects", "project"}, allEntries = true)
    public ProjectResponse create(ProjectRequest request) {
        Project saved = projectRepository.save(projectMapper.toEntity(request));
        log.info("Created project: {}", saved.getId());
        return projectMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "featuredProjects", "project"}, key = "#id")
    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = findOrThrow(id);
        projectMapper.updateEntity(request, project);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "featuredProjects", "project"}, key = "#id")
    public ProjectResponse uploadThumbnail(UUID id, MultipartFile file) {
        Project project = findOrThrow(id);

        // Delete old image from Cloudinary if exists
        deleteOldImage(project.getThumbnailUrl());

        String url = cloudinaryService.uploadImage(file, "projects");
        project.setThumbnailUrl(url);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "featuredProjects", "project"}, key = "#id")
    public void delete(UUID id) {
        Project project = findOrThrow(id);
        // Delete thumbnail
        deleteOldImage(project.getThumbnailUrl());
        // Delete all project images from Cloudinary
        project.getImages().forEach(img -> cloudinaryService.deleteByUrl(img.getImageUrl()));
        projectRepository.delete(project);
        log.info("Deleted project: {}", id);
    }

    // ── Upload project image ───────────────────────────────────

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "featuredProjects", "project"}, key = "#projectId")
    public ProjectResponse uploadImage(UUID projectId, MultipartFile file, String caption, Boolean isPrimary) {
        Project project = findOrThrow(projectId);

        // Upload to Cloudinary
        String imageUrl = cloudinaryService.uploadImage(file, "projects/images");

        // If this is primary, unset other primary images
        if (Boolean.TRUE.equals(isPrimary)) {
            project.getImages().forEach(img -> img.setIsPrimary(false));
        }

        // Create and save project image
        ProjectImage projectImage = ProjectImage.builder()
                .project(project)
                .imageUrl(imageUrl)
                .caption(caption)
                .displayOrder(project.getImages().size())
                .isPrimary(isPrimary != null ? isPrimary : false)
                .build();

        project.getImages().add(projectImage);
        projectRepository.save(project);

        log.info("Uploaded image for project: {}", projectId);
        return projectMapper.toResponse(project);
    }

    // ── Delete project image ───────────────────────────────────

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "featuredProjects", "project"}, key = "#projectId")
    public void deleteImage(UUID projectId, UUID imageId) {
        Project project = findOrThrow(projectId);

        ProjectImage image = project.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> AppException.notFound("Project image", imageId));

        // Delete from Cloudinary
        cloudinaryService.deleteByUrl(image.getImageUrl());

        // Remove from project
        project.getImages().remove(image);
        projectRepository.save(project);

        log.info("Deleted image {} from project: {}", imageId, projectId);
    }

    @Override
    @Cacheable(value = "project", key = "#id")
    public ProjectResponse getById(UUID id) {
        return projectMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Cacheable(value = "projects", unless = "#result == null")
    public List<ProjectResponse> getAll() {
        return projectRepository.findByStatusOrderByDisplayOrderAsc(ProjectStatus.ACTIVE)
                .stream().map(projectMapper::toResponse).toList();
    }

    @Override
    @Cacheable(value = "featuredProjects", unless = "#result == null")
    public List<ProjectResponse> getFeatured() {
        return projectRepository.findByIsFeaturedTrueOrderByDisplayOrderAsc()
                .stream().map(projectMapper::toResponse).toList();
    }

    @Override
    public PageResponse<ProjectResponse> getByStatus(ProjectStatus status, Pageable pageable) {
        Page<Project> page = status != null
                ? projectRepository.findByStatus(status, pageable)
                : projectRepository.findAll(pageable);
        return PageResponse.of(page.map(projectMapper::toResponse));
    }

    @Override
    @Cacheable(value = "projects", key = "#keyword + '-' + #pageable.pageNumber")
    public PageResponse<ProjectResponse> search(String keyword, Pageable pageable) {
        Page<Project> page = projectRepository.searchByKeyword(keyword, pageable);
        return PageResponse.of(page.map(projectMapper::toResponse));
    }

    // ── Private ────────────────────────────────────────────────

    private Project findOrThrow(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Project", id));
    }

    private void deleteOldImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String withVersion = parts[1];
                // Remove version prefix (v1234567/)
                String publicIdWithExt = withVersion.replaceFirst("v\\d+/", "");
                // Remove file extension
                String publicId = publicIdWithExt.substring(0, publicIdWithExt.lastIndexOf('.'));
                cloudinaryService.delete(publicId);
            }
        } catch (Exception ex) {
            log.warn("Could not parse Cloudinary public_id from URL: {}", imageUrl);
        }
    }
}