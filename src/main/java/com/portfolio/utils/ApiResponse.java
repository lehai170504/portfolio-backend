package com.portfolio.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final int status;
    private final String message;
    private final T data;
    private final Object errors;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    // ── success responses ──────────────────────────────────────

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.CREATED.value())
                .message("Created successfully")
                .data(data)
                .build();
    }

    public static ApiResponse<Void> noContent() {
        return ApiResponse.<Void>builder()
                .success(true)
                .status(HttpStatus.NO_CONTENT.value())
                .message("Deleted successfully")
                .build();
    }

    // ── error responses ────────────────────────────────────────

    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, Object errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .errors(errors)
                .build();
    }
}