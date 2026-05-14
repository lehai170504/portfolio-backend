package com.portfolio.controller;

import com.portfolio.dto.request.ProfileRequest;
import com.portfolio.dto.response.ProfileResponse;
import com.portfolio.service.ProfileService;
import com.portfolio.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Public portfolio profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Get public portfolio profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getPublicProfile() {
        return ResponseEntity.ok(ApiResponse.success(profileService.getPublicProfile()));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update public portfolio profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> update(
            @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                profileService.update(request),
                "Profile updated successfully"
        ));
    }

    @PatchMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload public profile avatar")
    public ResponseEntity<ApiResponse<ProfileResponse>> uploadAvatar(
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(
                profileService.uploadAvatar(file),
                "Avatar uploaded successfully"
        ));
    }

    @PatchMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload public profile resume PDF")
    public ResponseEntity<ApiResponse<ProfileResponse>> uploadResume(
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(
                profileService.uploadResume(file),
                "Resume uploaded successfully"
        ));
    }
}
