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
    private static final String INCREMENT_FIELD_SCRIPT = """
                if redis.call('EXISTS', KEYS[1]) == 0 then
                    return nil;
                end;
                local currentValue = redis.call('HGET', KEYS[1], ARGV[1]);
                if not currentValue then
                    currentValue = 0;
                end;
                local newValue = tonumber(currentValue) + 1;
                redis.call('HSET', KEYS[1], ARGV[1], newValue);
                return newValue;
            """;
    private static final String POST_COLLECTION_NAME = "Post";
    private static final String VIEWS_FIELD = "views";
    private static final String LIKES_FIELD = "likes";

    private final RedisTemplate<String, String> redisTemplate;

    public void incrementViews(long postId) {
        Long updatedViews = incrementField(postId, VIEWS_FIELD);
        if (updatedViews == null) {
            log.warn("Did not increment views for post with id {} because it is not cached", postId);
        } else {
            log.info("Incremented views for post with id {} new views value is {}", postId, updatedViews);
        }
    }

    public void incrementLikes(long postId) {
        Long updatedLikes = incrementField(postId, LIKES_FIELD);
        if (updatedLikes == null) {
            log.warn("Did not increment likes for post with id {} because it is not cached", postId);
        } else {
            log.info("Incremented likes for post with id {} new likes value is {}", postId, updatedLikes);
        }
    }

    private Long incrementField(long postId, String fieldName) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(INCREMENT_FIELD_SCRIPT);
        redisScript.setResultType(Long.class);
        String key = POST_COLLECTION_NAME + ":" + postId;
        return redisTemplate.execute(redisScript, Collections.singletonList(key), fieldName);
    }
}
