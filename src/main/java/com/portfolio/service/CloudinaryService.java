package com.portfolio.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.portfolio.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:portfolio}")
    private String folder;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final Set<String> ALLOWED_DOC_TYPES = Set.of(
            "application/pdf"
    );
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024L;  // 5 MB
    private static final long MAX_DOC_SIZE   = 10 * 1024 * 1024L; // 10 MB

    // ── Upload image ───────────────────────────────────────────

    public String uploadImage(MultipartFile file, String subFolder) {
        validateFile(file, ALLOWED_IMAGE_TYPES, MAX_IMAGE_SIZE,
                "Only JPEG, PNG, WEBP, GIF images are allowed", "Image exceeds 5 MB limit");

        return upload(file, folder + "/" + subFolder, "image");
    }

    // ── Upload document (PDF) ──────────────────────────────────

    public String uploadDocument(MultipartFile file, String subFolder) {
        validateFile(file, ALLOWED_DOC_TYPES, MAX_DOC_SIZE,
                "Only PDF documents are allowed", "Document exceeds 10 MB limit");

        return upload(file, folder + "/" + subFolder, "raw");
    }

    // ── Delete by public_id ────────────────────────────────────

    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted Cloudinary asset: {}", publicId);
        } catch (IOException ex) {
            log.warn("Failed to delete Cloudinary asset {}: {}", publicId, ex.getMessage());
        }
    }

    // ── Delete by URL ──────────────────────────────────────────

    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;
        try {
            // Extract public_id from URL
            // URL format: https://res.cloudinary.com/{cloud}/image/upload/v{version}/{folder}/{public_id}.{ext}
            String publicId = extractPublicIdFromUrl(url);
            if (publicId != null) {
                delete(publicId);
            }
        } catch (Exception ex) {
            log.warn("Failed to extract public_id from URL {}: {}", url, ex.getMessage());
        }
    }

    private String extractPublicIdFromUrl(String url) {
        try {
            // Remove query params
            url = url.split("\\?")[0];
            // Find the last part after '/upload/'
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String afterUpload = url.substring(uploadIndex + 8);
            // Remove version prefix (v1234567890/)
            if (afterUpload.startsWith("v")) {
                int slashIndex = afterUpload.indexOf('/');
                if (slashIndex != -1) {
                    afterUpload = afterUpload.substring(slashIndex + 1);
                }
            }
            // Remove file extension
            int dotIndex = afterUpload.lastIndexOf('.');
            if (dotIndex != -1) {
                afterUpload = afterUpload.substring(0, dotIndex);
            }
            return afterUpload;
        } catch (Exception ex) {
            return null;
        }
    }

    // ── Private helpers ────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String upload(MultipartFile file, String folderPath, String resourceType) {
        try {
            String publicId = folderPath + "/" + UUID.randomUUID();

            Map<String, Object> params = ObjectUtils.asMap(
                    "public_id",     publicId,
                    "resource_type", resourceType,
                    "overwrite",     true,
                    "quality",       "auto",
                    "fetch_format",  "auto"
            );

            Map<String, Object> result = cloudinary.uploader()
                    .upload(file.getBytes(), params);

            String url = (String) result.get("secure_url");
            log.info("Uploaded to Cloudinary: {}", url);
            return url;

        } catch (IOException ex) {
            log.error("Cloudinary upload failed: {}", ex.getMessage());
            throw new AppException("Failed to upload file to Cloudinary: " + ex.getMessage(),
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateFile(MultipartFile file, Set<String> allowedTypes,
                              long maxSize, String typeError, String sizeError) {
        if (file == null || file.isEmpty()) {
            throw AppException.badRequest("File is empty");
        }
        if (!allowedTypes.contains(file.getContentType())) {
            throw AppException.badRequest(typeError);
        }
        if (file.getSize() > maxSize) {
            throw AppException.badRequest(sizeError);
        }
    }
}