package com.portfolio.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatus httpStatus;

    public AppException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    // Convenience factories

    public static AppException notFound(String resource, UUID id) {
        return new AppException(resource + " not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public static AppException notFound(String message) {
        return new AppException(message, HttpStatus.NOT_FOUND);
    }

    public static AppException badRequest(String message) {
        return new AppException(message, HttpStatus.BAD_REQUEST);
    }

    public static AppException unauthorized(String message) {
        return new AppException(message, HttpStatus.UNAUTHORIZED);
    }

    public static AppException forbidden(String message) {
        return new AppException(message, HttpStatus.FORBIDDEN);
    }

    public static AppException conflict(String message) {
        return new AppException(message, HttpStatus.CONFLICT);
    }
}