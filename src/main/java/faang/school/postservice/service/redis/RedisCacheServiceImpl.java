package faang.school.postservice.service.redis;

import faang.school.postservice.dto.post.PostRedis;
import faang.school.postservice.dto.user.UserNFDto;
import faang.school.postservice.exception.RedisTransactionFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    private final Environment environment;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final RedisScript<Long> INCREMENT_VIEWS_SCRIPT = RedisScript.of(
            """
            local key = KEYS[1]
            local ttl = tonumber(ARGV[1])
            
            local post = redis.call('GET', key)
            if not post then
                return 0
            end
            
            local postObj = cjson.decode(post)
            postObj.views = postObj.views + 1
            redis.call('SET', key, cjson.encode(postObj))
            redis.call('EXPIRE', key, ttl)
            
            return postObj.views
            """,
            Long.class
    );

    @Override
    public void savePost(PostRedis post) {
        redisTemplate.opsForValue().set(
                environment.getRequiredProperty("spring.data.redis.prefix-posts") + post.getId(),
                post,
                Duration.ofDays(Long.parseLong(environment.getRequiredProperty("spring.data.redis.ttl-posts")))
        );
    }

    @Override
    public void saveUser(UserNFDto user) {
        redisTemplate.opsForValue().set(
                environment.getRequiredProperty("spring.data.redis.prefix-users") + user.getId(),
                user,
                Duration.ofDays(Long.parseLong(environment.getRequiredProperty("spring.data.redis.ttl-users")))
        );
    }


    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = RedisTransactionFailedException.class
    )
    @Override
    public void incrementPostViews(Long postId) {
        String key = environment.getRequiredProperty("spring.data.redis.prefix-posts") + postId;
        int ttl = Integer.parseInt(environment.getRequiredProperty("spring.data.redis.ttl-posts")) * 86400;
        try {
            Long updatedViews = redisTemplate.execute(
                    INCREMENT_VIEWS_SCRIPT,
                    Collections.singletonList(key),
                    ttl
            );

            if (updatedViews == null || updatedViews == 0) {
                log.warn("Post {} not found in Redis", postId);
            } else {
                log.info("Updated views for post {}: {}", postId, updatedViews);
            }
        } catch (RedisTransactionFailedException e) {
            log.error("Redis transaction failed", e);
            throw e;
        }
    }
}
