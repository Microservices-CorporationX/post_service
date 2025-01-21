package faang.school.postservice.repository.rediscache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedRedisRepository {

    private final RedisTemplate<String, List<String>> feedRedisTemplate;

    private static final int MAX_FEED_SIZE = 100;
    private static final long TTL_SECONDS = 86400;

    public void save(String key, String postId) {
        List<String> postIds = feedRedisTemplate.opsForValue().get(key);

        if (postIds == null) {
            postIds = new ArrayList<>();
        }

        postIds.add(0, postId);

        if (postIds.size() > MAX_FEED_SIZE) {
            postIds.remove(postIds.size() - 1);
        }

        feedRedisTemplate.opsForValue().set(key, postIds, Duration.ofSeconds(TTL_SECONDS));
    }

    public List<String> getPostIdsFromCache(String userId) {
        return feedRedisTemplate.opsForValue().get(userId);
    }
}
