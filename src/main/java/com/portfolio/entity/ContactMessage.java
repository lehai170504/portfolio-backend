package com.portfolio.entity;

import com.portfolio.enums.ContactStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contact_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactMessage extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String senderName;

    @Column(nullable = false, length = 150)
    private String senderEmail;

    @Column(length = 200)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ContactStatus status = ContactStatus.UNREAD;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;
}