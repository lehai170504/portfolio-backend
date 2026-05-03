package com.portfolio.service;

import com.portfolio.dto.request.ChangePasswordRequest;
import com.portfolio.dto.request.LoginRequest;
import com.portfolio.dto.response.AdminProfileResponse;
import com.portfolio.dto.response.AuthResponse;
import com.portfolio.entity.User;
import com.portfolio.exception.AppException;
import com.portfolio.repository.UserRepository;
import com.portfolio.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    // ── Login ──────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = findUserByEmail(userDetails.getUsername());

        String accessToken  = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        log.info("User logged in: {}", userDetails.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationMs())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    // ── Refresh token ──────────────────────────────────────────

    public AuthResponse refresh(String refreshToken) {
        try {
            String email = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                throw AppException.unauthorized("Refresh token is invalid or expired");
            }
            String newAccessToken = jwtService.generateAccessToken(userDetails);
            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtService.getExpirationMs())
                    .email(email)
                    .build();
        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            throw AppException.unauthorized("Invalid refresh token");
        }
    }

    // ── Get current admin profile ──────────────────────────────

    public AdminProfileResponse getMe(String email) {
        User user = findUserByEmail(email);
        return AdminProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // ── Upload avatar ──────────────────────────────────────────

    @Transactional
    public String uploadAvatar(String email, MultipartFile file) {
        User user = findUserByEmail(email);

        // Delete old avatar if exists
        if (user.getAvatarUrl() != null) {
            cloudinaryService.deleteByUrl(user.getAvatarUrl());
        }

        // Upload new avatar
        String avatarUrl = cloudinaryService.uploadImage(file, "avatars");
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        log.info("Avatar updated for user: {}", email);
        return avatarUrl;
    }

    // ── Update profile ─────────────────────────────────────────

    @Transactional
    public AdminProfileResponse updateProfile(String email, String fullName) {
        User user = findUserByEmail(email);
        user.setFullName(fullName);
        userRepository.save(user);
        log.info("Profile updated for user: {}", email);
        return getMe(email);
    }

    // ── Change password ────────────────────────────────────────

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw AppException.badRequest("New password and confirm password do not match");
        }
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw AppException.badRequest("New password must be different from current password");
        }
        User user = findUserByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw AppException.badRequest("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for: {}", email);
    }

    // ── Private ────────────────────────────────────────────────

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not found: " + email));
    }
}