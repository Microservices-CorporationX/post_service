package faang.school.postservice.service.redis;

import faang.school.postservice.dto.post.PostRedis;
import faang.school.postservice.dto.user.UserNFDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    @Value("${spring.data.redis.ttl-users}")
    private int ttlUsers = 1;
    @Value("${spring.data.redis.key-hash-users}")
    private String keyHashUsers;

    @Value("${spring.data.redis.ttl-posts}")
    private int ttlPosts = 1;
    @Value("${spring.data.redis.key-hash-posts}")
    private String keyHashPosts;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void savePost(PostRedis post) {
        redisTemplate.opsForHash().put(keyHashPosts, String.valueOf(post.getId()), post);
    }

    @Override
    public void saveUser(UserNFDto user) {
        redisTemplate.opsForHash().put(keyHashUsers, String.valueOf(user.getId()), user);
    }

    @PostConstruct
    public void load() {
        redisTemplate.expire(keyHashPosts, Duration.ofDays(ttlPosts));
        redisTemplate.expire(keyHashUsers, Duration.ofDays(ttlUsers));
    }
}
