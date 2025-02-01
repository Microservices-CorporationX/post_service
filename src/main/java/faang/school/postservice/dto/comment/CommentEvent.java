package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {
    private long authorId;
    private long postId;
    private long commentId;
    private String content;
    private LocalDateTime createdAt;
}
