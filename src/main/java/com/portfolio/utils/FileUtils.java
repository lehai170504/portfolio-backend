package com.portfolio.utils;

import com.portfolio.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

public final class FileUtils {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final Set<String> ALLOWED_DOC_TYPES = Set.of(
            "application/pdf"
    );
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024L;  // 5 MB
    private static final long MAX_DOC_SIZE   = 10 * 1024 * 1024L; // 10 MB

    private FileUtils() {}

    public static String storeImage(MultipartFile file, String uploadDir) {
        validateImage(file);
        return store(file, uploadDir, "images");
    }

    public static String storeDocument(MultipartFile file, String uploadDir) {
        validateDocument(file);
        return store(file, uploadDir, "documents");
    }

    public static void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            // Log but don't throw — best-effort cleanup
        }
    }

    // ── private helpers ────────────────────────────────────────

    private static String store(MultipartFile file, String baseDir, String subDir) {
        try {
            String ext = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + "." + ext;

            Path targetDir = Paths.get(baseDir, subDir);
            Files.createDirectories(targetDir);

            Path targetPath = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return subDir + "/" + filename;
        } catch (IOException ex) {
            throw new AppException("Failed to store file: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException("File is empty", HttpStatus.BAD_REQUEST);
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new AppException("Invalid image type. Allowed: JPEG, PNG, WEBP, GIF", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new AppException("Image exceeds max size of 5 MB", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException("File is empty", HttpStatus.BAD_REQUEST);
        }
        if (!ALLOWED_DOC_TYPES.contains(file.getContentType())) {
            throw new AppException("Only PDF documents are allowed", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_DOC_SIZE) {
            throw new AppException("Document exceeds max size of 10 MB", HttpStatus.BAD_REQUEST);
        }
    }

    private static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}