package faang.school.postservice.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentEventDto {
    private Long postAuthorId;
    private Long commentAuthorId;
    private Long postId;
    private Long commentId;
    private String commentContent;
    private LocalDateTime commentedAt;
}