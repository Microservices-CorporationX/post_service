package faang.school.postservice.model.redis;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.RMap;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "posts")
public class CachePost {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likeIds;
    private List<Long> commentIds;
    private Long adId;
    private List<Long> resourceIds;
    private LocalDateTime publishedAt;
    private long numLikes;
    private long numViews;
    private CopyOnWriteArraySet<CommentDto> comments;
    private RMap<String, Integer> version;

    public void incrementNumLikes() {
        numLikes++;
    }

    public void incrementNumViews() {
        numViews++;
    }
}
