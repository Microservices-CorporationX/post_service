package faang.school.postservice.redis.repository;

import faang.school.postservice.config.redis.JedisConfig;
import faang.school.postservice.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class PostCacheRepository {

    private final RedisTemplate<Long, Object> redisTemplate;
    private final JedisConfig redisConfig;

    public void cachePost(PostDto post) {
        redisTemplate.opsForValue().set(post.getId(), post, redisConfig.getUsersTtl(), TimeUnit.SECONDS);
    }

    public PostDto getPostBy(Long postId) {
        return (PostDto) redisTemplate.opsForValue().get(postId);
    }
}
