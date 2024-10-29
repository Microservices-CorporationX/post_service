package faang.school.postservice.model.redis;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "posts", timeToLive = 86400L)
public class RedisPost {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likeIds;
    private List<Long> commentIds;
    private List<Long> albumIds;
    private Long adId;
    private List<Long> resourceIds;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private long numLikes;
    private long numViews;
    private CopyOnWriteArraySet<CommentDto> comments;
    @Version
    private long version;

    public void incrementNumLikes() {
        numLikes++;
    }

    public void incrementNumViews() {
        numViews++;
    }
}
