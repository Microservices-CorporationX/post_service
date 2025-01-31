package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.TreeSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRedisEntity implements Serializable {
    public PostRedisEntity(Long postId, Long authorId, LocalDateTime createdAt) {
        this.postId = postId;
        this.authorId = authorId;
        this.createdAt = createdAt;
    }

    private Long postId;
    private Long authorId;
    private TreeSet<CommentEvent> comments;
    private LocalDateTime createdAt;
}
