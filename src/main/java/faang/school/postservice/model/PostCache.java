package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "Post", timeToLive = 84000)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCache {
    @Id
    private long id;
    private String content;
    private long authorId;
    private long projectId;
    private long likes;
    private long views;
    private LocalDateTime publishedAt;
}
