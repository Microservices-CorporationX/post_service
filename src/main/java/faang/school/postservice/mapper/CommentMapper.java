package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "post.id", source = "postId")
    Comment toEntity(CreateCommentDto createDto);

    @Mapping(target = "authorId", source = "editorId")
    Comment toUpdatedEntity(UpdateCommentDto updateDto);

    @Mapping(target = "likesId", source = "likes")
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    @IterableMapping(elementTargetType = Long.class)
    default List<Long> mapLikesToIds(List<Like> likes) {
        return Optional.ofNullable(likes)
                .orElse(Collections.emptyList())
                .stream()
                .map(Like::getId)
                .collect(Collectors.toList());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateCommentDto updateDto, @MappingTarget Comment comment);

}
