package faang.school.postservice.event;

import faang.school.postservice.dto.comment.CommentRedis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCommentEvent {
    private long postId;
    private CommentRedis comment;
}
