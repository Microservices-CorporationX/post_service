package faang.school.postservice.dto.comment;

/**
 * DTO for {@link faang.school.postservice.model.Comment}
 */
public record CommentResponse(Long id,
                              String content,
                              Long authorId,
                              Long postId) {
}