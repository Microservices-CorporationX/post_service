package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.model.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {
    PostCache toPostCache(PostDto postDto);

    PostDto toDto(PostCache postCache);
}