package faang.school.postservice.service.feed;

import faang.school.postservice.exception.RedisTransactionFailedException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RedisTemplate<String, Object> redisLettuceTemplate;
    private RedisScript<Long> feedUpdateScript;

    @Value("${spring.data.redis.prefix-feeds}")
    private String prefixFeed;

    @Value("${spring.data.redis.max-feeds}")
    private int maxFeeds;

    @Value("${spring.data.redis.ttl-feeds}")
    private int ttl;

    @PostConstruct
    public void init() {
        feedUpdateScript = RedisScript.of(
                """
                        local key = KEYS[1]
                        local postId = ARGV[1]
                        local ttlSeconds = ARGV[2]
                        local maxFeeds = tonumber(ARGV[3])
                        local score = tonumber(ARGV[4])
                        redis.call('ZADD', key, score, postId)
                        redis.call('EXPIRE', key, ttlSeconds)
                        local count = redis.call('ZCARD', key)
                        if count > maxFeeds then
                           local removeCount = count - maxFeeds
                           redis.call('ZREMRANGEBYRANK', key, 0, removeCount - 1)
                        end
                        return 1
                        """,
                Long.class
        );
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = RedisTransactionFailedException.class
    )
    public void bindPostToFollower(Long followerId, Long postId) {
        String key = prefixFeed + followerId;
        long currentTime = System.currentTimeMillis();
        List<String> keys = Collections.singletonList(key);
        Object[] args = {
                postId.toString(),
                ttl * 86400,
                maxFeeds,
                currentTime,
        };

        try {
            Long result = redisLettuceTemplate.execute(
                    feedUpdateScript,
                    keys,
                    args
            );
            if (result == null || result != 1) {
                throw new RedisTransactionFailedException(
                        "Failed to add post %s to feed %s".formatted(postId, key)
                );
            }
        } catch (Exception e) {
            log.error("Redis operation failed for post: {}", e.getMessage());
            throw new RedisTransactionFailedException(
                    "Redis operation failed for post %s".formatted(postId)
            );
        }
    }
}
