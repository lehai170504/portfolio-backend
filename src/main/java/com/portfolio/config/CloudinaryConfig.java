package com.portfolio.config;

import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class CloudinaryConfig {

    @Value("${app.cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${app.cloudinary.api-key:}")
    private String apiKey;

    @Value("${app.cloudinary.api-secret:}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        // Validation
        if (cloudName == null || cloudName.isBlank() ||
            apiKey == null || apiKey.isBlank() ||
            apiSecret == null || apiSecret.isBlank()) {
            log.error("Cloudinary configuration is incomplete! cloudName={}, apiKey={}, apiSecret={}",
                    cloudName, apiKey != null ? "***" : null, apiSecret != null ? "***" : null);
            throw new IllegalStateException(
                    "Cloudinary configuration is incomplete. Please set app.cloudinary.cloud-name, api-key, and api-secret");
        }

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", true);

        log.info("Cloudinary initialized with cloud_name: {}", cloudName);
        return new Cloudinary(config);
    }
}