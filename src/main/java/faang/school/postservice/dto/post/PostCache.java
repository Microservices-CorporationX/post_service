package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@RedisHash("Post")
@AllArgsConstructor
@NoArgsConstructor
public class PostCache implements Serializable {

    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Long viewsCount;
    private Long likesCount;
    private Long commentsCount;
    private List<Long> commentIds;
    private List<String> resourceKeys;
    private LocalDateTime publishedAt;
    @TimeToLive(unit = TimeUnit.DAYS)
    private Long ttl;
}
