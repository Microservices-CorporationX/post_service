package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.PostDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CacheFeedRepository implements RedisRepository {
    @Value("${spring.data.redis.feed-cache.key:feed:}")
    private String key;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Long> redisTemplateLong;
    @Value("${spring.data.redis.feed-cache.max_feed_size:500}")
    private long maxFeedSize;
    private ZSetOperations<String, Object> operations;
    private ZSetOperations<String, Long> operationsLong;

    @PostConstruct
    private void init() {
        operations = redisTemplate.opsForZSet();
        operationsLong = redisTemplateLong.opsForZSet();
    }

    @Override
    public void saveAll(Long id, List<PostDto> posts) {
        Set<ZSetOperations.TypedTuple<Long>> tuples = posts.stream()
                .map(post -> ZSetOperations.TypedTuple.of(post.getId(), (double) System.currentTimeMillis()))
                .collect(Collectors.toSet());
        operationsLong.add(key + id, tuples);
    }

    @Override
    public void add(Long followerId, Long postId) {
        operations.add(key + followerId, postId, System.currentTimeMillis());
    }

    @Override
    public Set<Long> find(Long id) {
        Set<Object> objects = operations.reverseRange(key + id, 0, maxFeedSize - 1);
        if (objects != null) {
            return objects.stream()
                    .map(obj -> Long.valueOf(String.valueOf(obj)))
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public Long getRank(Long id, Long postId) {
        return operations.rank(key + id, postId);
    }

    @Override
    public Set<Object> getRange(Long id, long startPostId, long endPostId) {
        return operations.range(key + id, startPostId, endPostId);
    }
}