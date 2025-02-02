package faang.school.postservice.model.cache;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "feed")
public class FeedCache {
    @Id
    private Long id;
    private RedisZSet<Long> postsId;
}
