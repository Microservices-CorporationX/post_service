package faang.school.postservice.repository.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FeedRepository {
    private static final String ADD_AND_TRIM_SCRIPT = """ 
            redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2])
            redis.call('ZREMRANGEBYRANK', KEYS[1], 0, -(tonumber(ARGV[3]) + 1))
            return 'OK'
            """;
    private static final String GET_FEED_SCRIPT = """
            local postId = ARGV[1]
            local pageSize = tonumber(ARGV[2])
            local rank = 0
            if postId == '' then
                rank = 0
            else
                rank = redis.call('ZRANK', KEYS[1], postId)
                if not rank then
                    return {}
                end
            end
            local posts = redis.call('ZREVRANGE', KEYS[1], -rank, pageSize)
            return posts
            """;

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${caching.feed.collection-name}")
    private String feedCollectionName;

    @Value("${caching.feed.size}")
    private int feedSize;

    @Value("${feed.page-size}")
    private int pageSize;

    public void addPostToUserFeed(long userId, long postId, LocalDateTime postPublishedAt) {
        RedisScript<String> script = RedisScript.of(ADD_AND_TRIM_SCRIPT, String.class);
        String key = feedCollectionName + ":" + userId;
        long timestamp = postPublishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();
        redisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(timestamp), String.valueOf(postId), String.valueOf(feedSize));
        log.info("Added post with id {} to feed for user with id {}", postId, userId);
    }

    public List<Long> getFeed(long userId, Long postId) {
        RedisScript<List> script = RedisScript.of(GET_FEED_SCRIPT, List.class);
        String key = feedCollectionName + ":" + userId;
        List posts = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                postId == null ? "" : String.valueOf(postId),
                String.valueOf(pageSize));
        if (posts == null || posts.isEmpty()) {
            return Collections.emptyList();
        }
        return posts.stream()
                .mapToLong(post -> Long.parseLong((String) post)).boxed().toList();
    }
}
