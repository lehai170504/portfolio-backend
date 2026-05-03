package com.portfolio.controller;

import com.portfolio.config.RateLimitConfig;
import com.portfolio.dto.request.ContactRequest;
import com.portfolio.dto.response.ContactResponse;
import com.portfolio.enums.ContactStatus;
import com.portfolio.service.ContactService;
import com.portfolio.utils.ApiResponse;
import com.portfolio.utils.PageResponse;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
@Tag(name = "Contact", description = "Contact form")
public class ContactController {

    private final ContactService  contactService;
    private final RateLimitConfig rateLimitConfig;

    @PostMapping
    @Operation(summary = "Submit contact form (public, rate limited: 3 requests/hour per IP)")
    public ResponseEntity<ApiResponse<ContactResponse>> submit(
            @Valid @RequestBody ContactRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = getClientIp(httpRequest);

        // Rate limit check
        Bucket bucket = rateLimitConfig.resolveContactBucket(ip);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error(429,
                            "Too many requests. You can send a maximum of 3 messages per hour."));
        }

        return ResponseEntity.status(201).body(
                ApiResponse.created(contactService.submit(request, ip))
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all messages (admin)")
    public ResponseEntity<ApiResponse<PageResponse<ContactResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ContactStatus status
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(contactService.getAll(status, pageable)));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get unread message count")
    public ResponseEntity<ApiResponse<Long>> countUnread() {
        return ResponseEntity.ok(ApiResponse.success(contactService.countUnread()));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mark message as read")
    public ResponseEntity<ApiResponse<ContactResponse>> markAsRead(
            @PathVariable java.util.UUID id) {
        return ResponseEntity.ok(ApiResponse.success(contactService.markAsRead(id)));
    }

    @PatchMapping("/{id}/replied")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mark message as replied")
    public ResponseEntity<ApiResponse<ContactResponse>> markAsReplied(
            @PathVariable java.util.UUID id) {
        return ResponseEntity.ok(ApiResponse.success(contactService.markAsReplied(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete message")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable java.util.UUID id) {
        contactService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    // ── Private ────────────────────────────────────────────────

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        return realIp != null ? realIp.trim() : request.getRemoteAddr();
    }
}