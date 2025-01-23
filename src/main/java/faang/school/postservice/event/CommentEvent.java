package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    private Long commentAuthorId;
    private Long postAuthorId;
    private Long postId;
    private Long commentId;
    private LocalDateTime createdAt;
}
