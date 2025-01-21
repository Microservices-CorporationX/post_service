package faang.school.postservice.repository.rediscache;

import faang.school.postservice.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class PostRedisRepository {

    private final RedisTemplate<String, PostDto> postRedisTemplate;
    private static final long TTL_SECONDS = 86400;

    public void save(String key, PostDto value){
        postRedisTemplate.opsForValue().set(key, value, Duration.ofSeconds(TTL_SECONDS));
    }

    public PostDto findPostByKey(String key){
        return postRedisTemplate.opsForValue().get(key);
    }
}
