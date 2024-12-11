package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPostEvent {
    private long commentId;
    private long authorId;
    private long postId;
    private LocalDateTime createdAt;
}
