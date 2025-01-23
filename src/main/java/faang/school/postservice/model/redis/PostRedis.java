package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.SortedSet;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Post")
public class PostRedis {

    @Id
    private String id;
    @TimeToLive
    private Long expirationInSeconds;

    private String content;

}
