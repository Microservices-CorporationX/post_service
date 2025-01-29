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
    private String content;
    private long authorId;
    private long postId;
    private LocalDateTime createdAt;
}
