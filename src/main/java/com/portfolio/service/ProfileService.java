package com.portfolio.service;

import com.portfolio.dto.request.ProfileRequest;
import com.portfolio.dto.response.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileResponse getPublicProfile();

    ProfileResponse update(ProfileRequest request);

    ProfileResponse uploadAvatar(MultipartFile file);

    ProfileResponse uploadResume(MultipartFile file);
}
