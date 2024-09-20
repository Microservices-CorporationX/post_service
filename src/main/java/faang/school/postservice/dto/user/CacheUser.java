package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("cacheUser")
public class CacheUser {
    @Id
    private long id;
    private LinkedHashSet<Long> postIds = new LinkedHashSet<>();

    @TimeToLive
    private long ttl;
}
