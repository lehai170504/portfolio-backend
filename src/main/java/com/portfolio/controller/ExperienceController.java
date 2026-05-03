package com.portfolio.controller;

import com.portfolio.dto.request.ExperienceRequest;
import com.portfolio.dto.response.ExperienceResponse;
import com.portfolio.enums.ExperienceType;
import com.portfolio.service.ExperienceService;
import com.portfolio.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/experiences")
@RequiredArgsConstructor
@Tag(name = "Experiences", description = "Work and education experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    @GetMapping
    @Operation(summary = "Get all experiences")
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(experienceService.getAll()));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get experiences by type")
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getByType(@PathVariable ExperienceType type) {
        return ResponseEntity.ok(ApiResponse.success(experienceService.getByType(type)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create experience")
    public ResponseEntity<ApiResponse<ExperienceResponse>> create(@Valid @RequestBody ExperienceRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(experienceService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update experience")
    public ResponseEntity<ApiResponse<ExperienceResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody ExperienceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(experienceService.update(id, request)));
    }

    @PatchMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload company logo")
    public ResponseEntity<ApiResponse<ExperienceResponse>> uploadLogo(
            @PathVariable UUID id, @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(experienceService.uploadLogo(id, file)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete experience")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        experienceService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}