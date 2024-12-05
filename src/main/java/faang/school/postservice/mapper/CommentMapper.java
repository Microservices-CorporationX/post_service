package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CreateCommentDto dto);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapToLikesIds")
    @Mapping(source = "post.id", target = "postId")
    CommentResponseDto toDto(Comment comment);

    List<CommentResponseDto> toListDto(List<Comment> comments);

    @Named("mapToLikesIds")
    default List<Long> mapToLikeIds(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream().map(Like::getId).toList();
    }
}