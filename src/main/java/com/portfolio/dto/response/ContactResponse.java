package com.portfolio.dto.response;

import com.portfolio.enums.ContactStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ContactResponse {
    private UUID id;
    private String senderName;
    private String senderEmail;
    private String subject;
    private String message;
    private ContactStatus status;
    private LocalDateTime createdAt;
}
