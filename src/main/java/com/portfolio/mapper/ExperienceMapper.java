package com.portfolio.mapper;

import com.portfolio.dto.request.ExperienceRequest;
import com.portfolio.dto.response.ExperienceResponse;
import com.portfolio.entity.Experience;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExperienceMapper {

    ExperienceResponse toResponse(Experience experience);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyLogoUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Experience toEntity(ExperienceRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyLogoUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ExperienceRequest request, @MappingTarget Experience experience);
}