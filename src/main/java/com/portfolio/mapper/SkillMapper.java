package com.portfolio.mapper;

import com.portfolio.dto.request.SkillRequest;
import com.portfolio.dto.response.SkillResponse;
import com.portfolio.entity.Skill;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SkillMapper {

    SkillResponse toResponse(Skill skill);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Skill toEntity(SkillRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(SkillRequest request, @MappingTarget Skill skill);
}