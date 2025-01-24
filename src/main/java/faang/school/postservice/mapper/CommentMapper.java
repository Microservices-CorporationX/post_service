package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.CreateCommentResponse;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.dto.comment.UpdatedCommentResponse;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "post", ignore = true)
    Comment toEntity(CreateCommentRequest commentRequest);

    @Mapping(source = "post.id", target = "postId")
    CreateCommentResponse toResponse(Comment comment);

    @Mapping(source = "post.id", target = "postId")
    UpdatedCommentResponse toUpdatedComment(Comment comment);


    void updateComment(@MappingTarget Comment comment, UpdateCommentRequest updateCommentRequest);
}
