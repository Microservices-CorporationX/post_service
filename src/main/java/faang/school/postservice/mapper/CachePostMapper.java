package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.RedisPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CachePostMapper {
    RedisPost toCache(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "content", source = "content")
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(Post post, @MappingTarget RedisPost redisPost);
}
