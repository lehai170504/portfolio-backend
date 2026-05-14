package com.portfolio.config;

import com.portfolio.entity.Profile;
import com.portfolio.entity.User;
import com.portfolio.enums.UserRole;
import com.portfolio.repository.ProfileRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@portfolio.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.admin.full-name:Portfolio Admin}")
    private String adminFullName;

    @Value("${app.profile.headline:Java Backend Developer}")
    private String profileHeadline;

    @Value("${app.profile.bio:I build clean, secure, and maintainable backend APIs with Spring Boot.}")
    private String profileBio;

    @Value("${app.profile.location:Vietnam}")
    private String profileLocation;

    @Override
    public void run(ApplicationArguments args) {
        seedAdmin();
        seedProfile();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin '{}' already exists - skipping seed", adminEmail);
            return;
        }

        User admin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .fullName(adminFullName)
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(admin);

        log.warn("Default admin account created. Email: {}. Change the password after first login.", adminEmail);
    }

    private void seedProfile() {
        if (profileRepository.findByProfileKey("main").isPresent()) {
            log.info("Public profile already exists - skipping seed");
            return;
        }

        Profile profile = Profile.builder()
                .profileKey("main")
                .fullName(adminFullName)
                .headline(profileHeadline)
                .bio(profileBio)
                .email(adminEmail)
                .location(profileLocation)
                .yearsOfExperience(0)
                .availability("Open to internship, fresher, and junior backend opportunities")
                .build();

        profileRepository.save(profile);
        log.info("Default public profile created");
    }
}
