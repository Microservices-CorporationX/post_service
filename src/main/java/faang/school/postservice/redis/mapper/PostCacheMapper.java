package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.redis.entity.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {
    PostCache toPostCache(PostDto postDto);

    PostDto toDto(PostCache postCache);

    default List<CommentDto> mapCopyOnWriteArrayListToList(CopyOnWriteArraySet<CommentDto> comments) {
        return new ArrayList<>(comments);
    }

    default CopyOnWriteArraySet<CommentDto> mapListToCopyOnWriteArrayList(List<CommentDto> comments) {
        return new CopyOnWriteArraySet<>(comments);
    }
}
