package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.PostDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CacheRedisRepository implements RedisRepository {

    private static final String KEY = "feed:";
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.feed-cache.max_feed_size:500}")
    private long maxFeedSize;
    private ZSetOperations<String, Object> operations;

    @PostConstruct
    private void init() {
        operations = redisTemplate.opsForZSet();
    }

    @Override
    public void saveAll(Long id, List<PostDto> posts) {
        posts.forEach(post -> operations.add(KEY + id, post.getId(), System.currentTimeMillis()));
    }

    @Override
    public void add(Long followerId, Long postId) {
        operations.add(KEY + followerId, postId, System.currentTimeMillis());
    }

    @Override
    public Set<Long> find(Long id) {
        Set<Object> objects = operations.reverseRange(KEY + id, 0, maxFeedSize - 1);
        if (!(objects == null)) {
            return objects.stream()
                    .map(obj -> Long.valueOf(String.valueOf(obj)))
                    .collect(Collectors.toSet());
        }
        return null;
    }

    @Override
    public Long getRank(Long id, Long postId) {
        return operations.rank(KEY + id, postId);
    }

    @Override
    public Set<Object> getRange(Long id, long startPostId, long endPostId) {
        return operations.range(KEY + id, startPostId, endPostId);
    }
}