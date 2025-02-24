package ru.corporationx.postservice.mapper;

import ru.corporationx.postservice.dto.resource.ResourceDto;
import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(target = "postId", source = "post", qualifiedByName = "post")
    ResourceDto toDto(Resource resource);

    @Named("post")
    default Long mapPostId(Post post) {
        return post.getId();
    }
}
