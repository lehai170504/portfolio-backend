package com.portfolio.service.impl;

import com.portfolio.dto.request.ContactRequest;
import com.portfolio.dto.response.ContactResponse;
import com.portfolio.entity.ContactMessage;
import com.portfolio.enums.ContactStatus;
import com.portfolio.exception.AppException;
import com.portfolio.mapper.ContactMapper;
import com.portfolio.repository.ContactRepository;
import com.portfolio.service.ContactService;
import com.portfolio.service.EmailService;
import com.portfolio.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public ContactResponse submit(ContactRequest request, String ipAddress) {
        ContactMessage message = contactMapper.toEntity(request);
        message.setIpAddress(ipAddress);
        ContactMessage saved = contactRepository.save(message);

        // Send email notifications asynchronously
        emailService.sendContactNotification(saved);
        emailService.sendAutoReply(saved);

        log.info("Contact message saved with id: {}", saved.getId());
        return contactMapper.toResponse(saved);
    }

    @Override
    public PageResponse<ContactResponse> getAll(ContactStatus status, Pageable pageable) {
        if (status != null) {
            return PageResponse.of(
                    contactRepository.findByStatus(status, pageable)
                            .map(contactMapper::toResponse)
            );
        }
        return PageResponse.of(
                contactRepository.findAll(pageable).map(contactMapper::toResponse)
        );
    }

    @Override
    @Transactional
    public ContactResponse markAsRead(UUID id) {
        ContactMessage message = findOrThrow(id);
        message.setStatus(ContactStatus.READ);
        return contactMapper.toResponse(contactRepository.save(message));
    }

    @Override
    @Transactional
    public ContactResponse markAsReplied(UUID id) {
        ContactMessage message = findOrThrow(id);
        message.setStatus(ContactStatus.REPLIED);
        return contactMapper.toResponse(contactRepository.save(message));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        contactRepository.delete(findOrThrow(id));
    }

    @Override
    public long countUnread() {
        return contactRepository.countByStatus(ContactStatus.UNREAD);
    }

    private ContactMessage findOrThrow(UUID id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Contact message", id));
    }
}