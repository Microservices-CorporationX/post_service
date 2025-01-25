package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("User")
public class UserRedis {

    @Id
    private Long id;
    @TimeToLive
    private Long expirationInSeconds;
    private String name;

}
