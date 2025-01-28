package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentReadDto(
        long id,
        String content,
        long authorId,
        List<Long> likesId,
        long postId,
        LocalDateTime updatedAt
) {}
