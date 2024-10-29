package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("feed")
public class RedisFeed {
    @Id
    private Long followerId;
    private RedisZSet<Long> postIds;
}
