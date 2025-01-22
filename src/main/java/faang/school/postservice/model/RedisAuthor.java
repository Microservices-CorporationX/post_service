package faang.school.postservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "Author", timeToLive = 86400)
public class RedisAuthor {
    @Id
    private long id;
}
