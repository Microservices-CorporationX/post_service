package faang.school.postservice.mapper;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(target = "post", ignore = true)
    Resource toEntity(ResourceDto resourceDto);

    @Mapping(target = "postId", source = "post.id")
    ResourceDto toDto(Resource resource);
}
