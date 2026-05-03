package com.portfolio.controller;

import com.portfolio.dto.request.ProjectRequest;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.service.ProjectService;
import com.portfolio.service.ProjectViewService;
import com.portfolio.utils.ApiResponse;
import com.portfolio.utils.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectViewService projectViewService;

    // ── Public endpoints ───────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all active projects (public)")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(projectService.getAll()));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured projects (public)")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getFeatured() {
        return ResponseEntity.ok(ApiResponse.success(projectService.getFeatured()));
    }

    @GetMapping("/search")
    @Operation(summary = "Search projects by keyword (public)")
    public ResponseEntity<ApiResponse<PageResponse<ProjectResponse>>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("displayOrder").ascending());
        return ResponseEntity.ok(ApiResponse.success(projectService.search(keyword, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by id (public) - tracks view count")
    public ResponseEntity<ApiResponse<ProjectResponse>> getById(
            @PathVariable UUID id,
            HttpServletRequest request) {
        ProjectResponse response = projectService.getById(id);

        // Track view asynchronously
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        projectViewService.trackView(id, ip, userAgent);

        // Add view count to response
        response.setViewCount(projectViewService.getTotalViews(id));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        return realIp != null ? realIp.trim() : request.getRemoteAddr();
    }

    // ── Admin endpoints ────────────────────────────────────────

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all projects paginated (admin) — filter by status")
    public ResponseEntity<ApiResponse<PageResponse<ProjectResponse>>> getAllAdmin(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc")          String sortDir,
            @RequestParam(required = false) ProjectStatus status
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(projectService.getByStatus(status, pageable)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create project (admin)")
    public ResponseEntity<ApiResponse<ProjectResponse>> create(
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(projectService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update project (admin)")
    public ResponseEntity<ApiResponse<ProjectResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.success(projectService.update(id, request)));
    }

    @PatchMapping(value = "/{id}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload thumbnail to Cloudinary (admin)")
    public ResponseEntity<ApiResponse<ProjectResponse>> uploadThumbnail(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(
                projectService.uploadThumbnail(id, file), "Thumbnail uploaded to Cloudinary"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete project (admin)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        projectService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload project image/screenshot (admin)")
    public ResponseEntity<ApiResponse<ProjectResponse>> uploadProjectImage(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false, defaultValue = "false") Boolean isPrimary) {
        return ResponseEntity.ok(ApiResponse.success(
                projectService.uploadImage(id, file, caption, isPrimary), "Image uploaded successfully"));
    }

    @DeleteMapping("/{projectId}/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete project image (admin)")
    public ResponseEntity<ApiResponse<Void>> deleteProjectImage(
            @PathVariable UUID projectId,
            @PathVariable UUID imageId) {
        projectService.deleteImage(projectId, imageId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}