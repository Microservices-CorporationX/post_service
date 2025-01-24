package faang.school.postservice.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdatedCommentResponse {
    private Long id;
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime updatedAt;
}
