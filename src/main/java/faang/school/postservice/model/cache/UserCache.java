package faang.school.postservice.model.cache;

import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@RequiredArgsConstructor
@RedisHash(value = "user")
public class UserCache implements Serializable {

    @Id
    private final Long id;
    private final String username;

    @TimeToLive
    @Value("${spring.data.redis.cache.ttl}")
    private long ttl;
}
