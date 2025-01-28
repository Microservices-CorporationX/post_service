package faang.school.postservice.repository.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.CommentCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentCacheRepository {
    public static final String ADD_AND_TRIM_SCRIPT = """ 
            redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2]);
            redis.call('ZREMRANGEBYRANK', KEYS[1], 0, -(tonumber(ARGV[3]) + 1));
            return 'OK';
            """;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${caching.comment.collection-name}")
    private String feedCollectionName;

    @Value("${caching.comment.size}")
    private int maxCommentsStored;

    public void save(long postId, CommentCache commentCache) {
        try {
            String serializedComment = objectMapper.writeValueAsString(commentCache);
            RedisScript<String> script = RedisScript.of(ADD_AND_TRIM_SCRIPT, String.class);
            String key = feedCollectionName + ":" + postId;
            long timestamp = commentCache.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            redisTemplate.execute(
                    script,
                    Collections.singletonList(key),
                    String.valueOf(timestamp), serializedComment, String.valueOf(maxCommentsStored));
            log.info("Added comment {} to cache for post with id {}", commentCache, postId);
        } catch (JsonProcessingException e) {
            log.error("Could not serialize CommentCache to json", e);
            throw new IllegalStateException("Could not serialize CommentCache to json");
        }
    }
}
