package com.portfolio.service.impl;

import com.portfolio.dto.request.ExperienceRequest;
import com.portfolio.dto.response.ExperienceResponse;
import com.portfolio.entity.Experience;
import com.portfolio.enums.ExperienceType;
import com.portfolio.exception.AppException;
import com.portfolio.mapper.ExperienceMapper;
import com.portfolio.repository.ExperienceRepository;
import com.portfolio.service.CloudinaryService;
import com.portfolio.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ExperienceMapper     experienceMapper;
    private final CloudinaryService    cloudinaryService;

    @Override
    @Transactional
    @CacheEvict(value = "experiences", allEntries = true)
    public ExperienceResponse create(ExperienceRequest request) {
        validateDates(request);
        Experience saved = experienceRepository.save(experienceMapper.toEntity(request));
        log.info("Created experience: {}", saved.getId());
        return experienceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "experiences", allEntries = true)
    public ExperienceResponse update(UUID id, ExperienceRequest request) {
        validateDates(request);
        Experience experience = findOrThrow(id);
        experienceMapper.updateEntity(request, experience);
        return experienceMapper.toResponse(experienceRepository.save(experience));
    }

    @Override
    @Transactional
    @CacheEvict(value = "experiences", allEntries = true)
    public ExperienceResponse uploadLogo(UUID id, MultipartFile file) {
        Experience experience = findOrThrow(id);
        deleteOldImage(experience.getCompanyLogoUrl());
        String url = cloudinaryService.uploadImage(file, "logos");
        experience.setCompanyLogoUrl(url);
        return experienceMapper.toResponse(experienceRepository.save(experience));
    }

    @Override
    @Transactional
    @CacheEvict(value = "experiences", allEntries = true)
    public void delete(UUID id) {
        Experience experience = findOrThrow(id);
        deleteOldImage(experience.getCompanyLogoUrl());
        experienceRepository.delete(experience);
    }

    @Override
    @Cacheable(value = "experiences", unless = "#result == null")
    public List<ExperienceResponse> getAll() {
        return experienceRepository.findAllByOrderByStartDateDesc()
                .stream().map(experienceMapper::toResponse).toList();
    }

    @Override
    @Cacheable(value = "experiences", key = "#type", unless = "#result == null")
    public List<ExperienceResponse> getByType(ExperienceType type) {
        return experienceRepository.findByTypeOrderByStartDateDesc(type)
                .stream().map(experienceMapper::toResponse).toList();
    }

    private void validateDates(ExperienceRequest request) {
        if (Boolean.TRUE.equals(request.getIsCurrent())) {
            request.setEndDate(null);
            return;
        }
        if (request.getEndDate() != null && request.getStartDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw AppException.badRequest("End date must not be before start date");
        }
    }

    private Experience findOrThrow(UUID id) {
        return experienceRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Experience", id));
    }

    private void deleteOldImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String publicIdWithExt = parts[1].replaceFirst("v\\d+/", "");
                String publicId = publicIdWithExt.substring(0, publicIdWithExt.lastIndexOf('.'));
                cloudinaryService.delete(publicId);
            }
        } catch (Exception ex) {
            log.warn("Could not parse Cloudinary public_id from URL: {}", imageUrl);
        }
    }
}