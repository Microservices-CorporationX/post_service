package faang.school.postservice.repository.feed;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FeedRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private ZSetOperations<String, Object> zSetOperations;

    @PostConstruct
    public void init() {
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public void addPostToFeed(Long userId, Long postId, long timestamp) {
        String feedKey = getFeedKey(userId);
        checkZSetSize(feedKey);
        zSetOperations.add(feedKey, postId, timestamp);
    }

    public Set<Object> getAllFeedAsc(Long userId) {
        String feedKey = getFeedKey(userId);
        return zSetOperations.reverseRange(feedKey, 0, -1);
    }

    private String getFeedKey(Long userId) {
        return "feed:" + userId;
    }

    private void checkZSetSize(String feedKey) {
        Long size = zSetOperations.size(feedKey);
        if (size >= 500) {
            zSetOperations.removeRange(feedKey, 0, 0);
        }
    }
}
