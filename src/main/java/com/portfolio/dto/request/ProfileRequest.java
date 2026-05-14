package com.portfolio.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class ProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Headline is required")
    @Size(max = 150, message = "Headline must not exceed 150 characters")
    private String headline;

    private String bio;

    @URL(message = "Invalid avatar URL format")
    private String avatarUrl;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 30, message = "Phone must not exceed 30 characters")
    private String phone;

    @Size(max = 120, message = "Location must not exceed 120 characters")
    private String location;

    @URL(message = "Invalid GitHub URL format")
    private String githubUrl;

    @URL(message = "Invalid LinkedIn URL format")
    private String linkedinUrl;

    @URL(message = "Invalid portfolio URL format")
    private String portfolioUrl;

    @URL(message = "Invalid resume URL format")
    private String resumeUrl;

    @Min(value = 0, message = "Years of experience must be at least 0")
    @Max(value = 50, message = "Years of experience must not exceed 50")
    private Integer yearsOfExperience = 0;

    @Size(max = 120, message = "Availability must not exceed 120 characters")
    private String availability;
}
