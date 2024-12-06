package faang.school.postservice.mapper;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(target = "postId", source = "post.id")
    ResourceDto toResourceDto(Resource resource);

    @Mapping(target = "post", ignore = true)
    Resource toEntity(ResourceDto resourceDto);

    List<ResourceDto> toResourceDto(List<Resource> resources);
    List<Resource> toEntity(List<ResourceDto> resourceDtos);
}
