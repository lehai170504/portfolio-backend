package com.portfolio.service;

import com.portfolio.dto.request.ContactRequest;
import com.portfolio.dto.response.ContactResponse;
import com.portfolio.enums.ContactStatus;
import com.portfolio.utils.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ContactService {
    ContactResponse submit(ContactRequest request, String ipAddress);
    PageResponse<ContactResponse> getAll(ContactStatus status, Pageable pageable);
    ContactResponse markAsRead(UUID id);
    ContactResponse markAsReplied(UUID id);
    void delete(UUID id);
    long countUnread();
}