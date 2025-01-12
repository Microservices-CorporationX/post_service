package faang.school.postservice.config.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisTtlConfig {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.time_to_live.post}")
    private long postTimeToLive;
    @Value("${spring.data.redis.time_to_live.user}")
    private long userTimeToLive;

    @PostConstruct
    public void setTTLForPosts() {
        redisTemplate.expire("posts", Duration.ofSeconds(postTimeToLive));
        redisTemplate.expire("users", Duration.ofSeconds(userTimeToLive));
    }
}