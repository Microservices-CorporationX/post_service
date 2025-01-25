package faang.school.postservice.model.redis;

import faang.school.postservice.dto.comment.CommentRedisDto;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;
import java.util.SortedSet;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Post")
public class PostRedis {

    @Id
    private Long id;
    @TimeToLive
    private Long expirationInSeconds;

    private String content;
    private Long authorId;
    private LocalDateTime publishAt;

    private SortedSet<CommentRedisDto> tags;

    @Version
    private Long version;

}
