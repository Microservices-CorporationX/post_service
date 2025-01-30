package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likesCount", source = "likes", qualifiedByName = "mapLikeToCount")
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    @Mapping(target = "likesCount", source = "likes", qualifiedByName = "mapLikeToCount")
    @Mapping(target = "commentsCount", source = "comments", qualifiedByName = "mapCommentToCount")
    @Mapping(target = "resourceKeys", source = "resources", qualifiedByName = "mapResourcesToKeys")
    @Mapping(target = "commentIds", source = "comments", qualifiedByName = "mapToCommentIds")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    PostCache toCache(Post post);

    @Named("mapLikeToCount")
    default Long mapLike(List<Like> likes) {
        if (likes == null) {
            return 0L;
        }
        return (long) likes.size();
    }

    @Named("mapCommentToCount")
    default Long mapComment(List<Comment> comments) {
        if (comments == null) {
            return 0L;
        }
        return (long) comments.size();
    }

    @Named("mapResourcesToKeys")
    default List<String> mapResources(List<Resource> resources) {
        if (resources == null) {
            return null;
        }
        return resources.stream()
                .map(Resource::getKey)
                .toList();
    }

    @Named("mapToCommentIds")
    default List<Long> mapToCommentIds(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }
}