package com.portfolio.service.impl;

import com.portfolio.dto.request.ProfileRequest;
import com.portfolio.dto.response.ProfileResponse;
import com.portfolio.entity.Profile;
import com.portfolio.exception.AppException;
import com.portfolio.mapper.ProfileMapper;
import com.portfolio.repository.ProfileRepository;
import com.portfolio.service.CloudinaryService;
import com.portfolio.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private static final String MAIN_PROFILE_KEY = "main";

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Cacheable(value = "profile", key = "'main'")
    public ProfileResponse getPublicProfile() {
        return profileMapper.toResponse(findMainProfile());
    }

    @Override
    @Transactional
    @CacheEvict(value = "profile", key = "'main'")
    public ProfileResponse update(ProfileRequest request) {
        Profile profile = profileRepository.findByProfileKey(MAIN_PROFILE_KEY)
                .orElseGet(() -> {
                    Profile created = profileMapper.toEntity(request);
                    created.setProfileKey(MAIN_PROFILE_KEY);
                    return created;
                });

        profileMapper.updateEntity(request, profile);
        Profile saved = profileRepository.save(profile);
        log.info("Updated public profile: {}", saved.getId());
        return profileMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "profile", key = "'main'")
    public ProfileResponse uploadAvatar(MultipartFile file) {
        Profile profile = findMainProfile();
        cloudinaryService.deleteByUrl(profile.getAvatarUrl());
        profile.setAvatarUrl(cloudinaryService.uploadImage(file, "profile/avatar"));
        return profileMapper.toResponse(profileRepository.save(profile));
    }

    @Override
    @Transactional
    @CacheEvict(value = "profile", key = "'main'")
    public ProfileResponse uploadResume(MultipartFile file) {
        Profile profile = findMainProfile();
        cloudinaryService.deleteByUrl(profile.getResumeUrl(), "raw");
        profile.setResumeUrl(cloudinaryService.uploadDocument(file, "profile/resume"));
        return profileMapper.toResponse(profileRepository.save(profile));
    }

    private Profile findMainProfile() {
        return profileRepository.findByProfileKey(MAIN_PROFILE_KEY)
                .orElseThrow(() -> AppException.notFound("Public profile not found"));
    }
}
