package ru.corporationx.postservice.mapper.comment;

import ru.corporationx.postservice.dto.comment.CommentDto;
import ru.corporationx.postservice.model.Comment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    void update(@MappingTarget Comment comment, CommentDto commentDto);
}
