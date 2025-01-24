package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentCache;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    void update(@MappingTarget Comment comment, CommentDto commentDto);

    @Mapping(target = "likesCount", source = "likes", qualifiedByName = "mapLikeToCount")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    CommentCache toCache(Comment comment);

    @Named("mapLikeToCount")
    default Long mapLike(List<Like> likes) {
        if (likes == null) {
            return 0L;
        }
        return (long) likes.size();
    }
}
