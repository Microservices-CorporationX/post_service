package faang.school.postservice.repository.rediscache;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class PostRedisRepository {

    private final RedisTemplate<String, Post> redisTemplate;
    private static final long TTL_SECONDS = 86400;

    public void save(String key, Post value){
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(TTL_SECONDS));
    }

    public Post findPostByKey(String key){
        return redisTemplate.opsForValue().get(key);
    }
}
