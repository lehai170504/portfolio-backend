package com.portfolio.repository;

import com.portfolio.entity.ContactMessage;
import com.portfolio.enums.ContactStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<ContactMessage, UUID> {
    Page<ContactMessage> findByStatus(ContactStatus status, Pageable pageable);
    long countByStatus(ContactStatus status);
}