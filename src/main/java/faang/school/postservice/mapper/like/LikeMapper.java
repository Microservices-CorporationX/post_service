package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss a")
    LikeDto toDto(Like like);

    @Mapping(target = "comment", expression = "java(mapCommentId(likeDto.getCommentId()))")
    @Mapping(target = "post", expression = "java(mapPostId(likeDto.getPostId()))")
    @Mapping(target = "createdAt", expression = "java(mapCreatedAt(likeDto.getCreatedAt()))")
    Like toEntity(LikeDto likeDto);

    default Post mapPostId(Long postId) {
        return Post.builder()
                .id(postId)
                .build();
    }

    default Comment mapCommentId(Long commentId) {
        return Comment.builder()
                .id(commentId)
                .build();
    }

    default LocalDateTime mapCreatedAt(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
        return LocalDateTime.parse(dateTimeString, formatter);
    }
}
