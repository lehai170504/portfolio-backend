package com.portfolio.mapper;

import com.portfolio.dto.request.ProjectRequest;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.entity.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ProjectImageMapper.class})
public interface ProjectMapper {

    @Mapping(source = "images", target = "images")
    ProjectResponse toResponse(Project project);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Project toEntity(ProjectRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ProjectRequest request, @MappingTarget Project project);
}