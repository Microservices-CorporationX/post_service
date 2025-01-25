package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponseDto(
        Long id,
        String content,
        Long authorId,
        List<Long> likeIds,
        Long postId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {  }
