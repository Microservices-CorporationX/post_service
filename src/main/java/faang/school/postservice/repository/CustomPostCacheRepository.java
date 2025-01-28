package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomPostCacheRepository {
    private static final String INCREMENT_VIEWS_SCRIPT = """
                if redis.call('EXISTS', KEYS[1]) == 0 then
                    return nil;
                end;
                local currentViews = redis.call('HGET', KEYS[1], ARGV[1]);
                if not currentViews then
                    currentViews = 0;
                end;
                local newViews = tonumber(currentViews) + 1;
                redis.call('HSET', KEYS[1], ARGV[1], newViews);
                return newViews;
            """;
    private static final String POST_COLLECTION_NAME = "Post";
    private static final String VIEWS_FIELD = "views";

    private final RedisTemplate<String, String> redisTemplate;

    public void incrementViews(long postId) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(INCREMENT_VIEWS_SCRIPT);
        redisScript.setResultType(Long.class);
        String key = POST_COLLECTION_NAME + ":" + postId;
        Long updatedViews = redisTemplate.execute(redisScript, Collections.singletonList(key), VIEWS_FIELD);
        if (updatedViews == null) {
            log.warn("Did not increment views for post with id {} because it is not cached", postId);
        } else {
            log.info("Incremented views for post with id {} new views value is {}", postId, updatedViews);
        }
    }
}
