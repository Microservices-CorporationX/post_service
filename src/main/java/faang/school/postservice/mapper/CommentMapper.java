package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentResponse;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    @Mapping(source = "postId", target = "post.id")
    @Mapping(source = "userId", target = "authorId")
    Comment toEntity(CreateCommentRequest createCommentRequest);

    @Mapping(source = "post.id", target = "postId")
    CommentResponse toCommentResponse(Comment comment);
}