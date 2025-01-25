package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "postId", source = "post.id")
    CommentEvent toEvent(Comment comment);

    void update(@MappingTarget Comment comment, CommentDto commentDto);
}
