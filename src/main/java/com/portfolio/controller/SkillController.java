package com.portfolio.controller;

import com.portfolio.dto.request.SkillRequest;
import com.portfolio.dto.response.SkillResponse;
import com.portfolio.service.SkillService;
import com.portfolio.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skills management")
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "Get all visible skills")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(skillService.getAll()));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get skills by category")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success(skillService.getByCategory(category)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create skill")
    public ResponseEntity<ApiResponse<SkillResponse>> create(@Valid @RequestBody SkillRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(skillService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update skill")
    public ResponseEntity<ApiResponse<SkillResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(ApiResponse.success(skillService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete skill")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        skillService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}