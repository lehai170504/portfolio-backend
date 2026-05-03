package com.portfolio.mapper;

import com.portfolio.dto.response.ProjectImageResponse;
import com.portfolio.entity.ProjectImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectImageMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "imageUrl", target = "imageUrl")
    @Mapping(source = "caption", target = "caption")
    @Mapping(source = "displayOrder", target = "displayOrder")
    @Mapping(source = "isPrimary", target = "isPrimary")
    @Mapping(source = "createdAt", target = "createdAt")
    ProjectImageResponse toResponse(ProjectImage projectImage);

    List<ProjectImageResponse> toResponseList(List<ProjectImage> projectImages);
}
