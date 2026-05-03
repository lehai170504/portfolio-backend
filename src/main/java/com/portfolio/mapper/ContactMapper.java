package com.portfolio.mapper;

import com.portfolio.dto.request.ContactRequest;
import com.portfolio.dto.response.ContactResponse;
import com.portfolio.entity.ContactMessage;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    ContactResponse toResponse(ContactMessage contactMessage);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "name", target = "senderName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ContactMessage toEntity(ContactRequest request);
}