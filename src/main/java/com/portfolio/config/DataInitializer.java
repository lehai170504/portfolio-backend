package com.portfolio.config;

import com.portfolio.entity.User;
import com.portfolio.enums.UserRole;
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
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@portfolio.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.admin.full-name:Portfolio Admin}")
    private String adminFullName;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin '{}' already exists — skipping seed", adminEmail);
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

        log.warn("╔══════════════════════════════════════════════╗");
        log.warn("║         DEFAULT ADMIN ACCOUNT CREATED        ║");
        log.warn("║  Email   : {}              ║", adminEmail);
        log.warn("║  Password: {}                       ║", adminPassword);
        log.warn("║  !! CHANGE PASSWORD AFTER FIRST LOGIN !!     ║");
        log.warn("╚══════════════════════════════════════════════╝");
    }
}