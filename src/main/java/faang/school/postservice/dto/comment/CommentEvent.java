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
public class CommentEvent implements Comparable<CommentEvent> {
    private long commentId;
    private long authorId;
    private long postId;
    private String content;
    private LocalDateTime createdAt;

    @Override
    public int compareTo(CommentEvent o) {
        return o.getCreatedAt().compareTo(this.getCreatedAt());
    }
}