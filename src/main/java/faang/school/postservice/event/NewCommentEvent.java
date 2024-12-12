package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentEvent {
    private Long postId;
    private long authorId;
    private long commentId;
    private LocalDateTime createdAt;
}
