package com.portfolio.controller;

import com.portfolio.dto.request.ChangePasswordRequest;
import com.portfolio.dto.request.LoginRequest;
import com.portfolio.dto.response.AdminProfileResponse;
import com.portfolio.dto.response.AuthResponse;
import com.portfolio.service.AuthService;
import com.portfolio.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Admin login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestParam String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(refreshToken)));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current admin profile")
    public ResponseEntity<ApiResponse<AdminProfileResponse>> getMe(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(authService.getMe(userDetails.getUsername())));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update admin profile")
    public ResponseEntity<ApiResponse<AdminProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String fullName) {
        return ResponseEntity.ok(ApiResponse.success(
                authService.updateProfile(userDetails.getUsername(), fullName),
                "Profile updated successfully"
        ));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload admin avatar (multipart/form-data)")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("file") MultipartFile file) {
        String avatarUrl = authService.uploadAvatar(userDetails.getUsername(), file);
        return ResponseEntity.ok(ApiResponse.success(avatarUrl, "Avatar uploaded successfully"));
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Change admin password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
}