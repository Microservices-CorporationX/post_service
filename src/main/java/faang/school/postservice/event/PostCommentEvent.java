package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentEvent {
    private long id;
    private long postId;
    private long authorId;
    private String content;
    private LocalDateTime createdAt;
}
