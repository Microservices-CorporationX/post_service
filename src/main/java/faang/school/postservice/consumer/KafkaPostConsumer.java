package faang.school.postservice.consumer;


import faang.school.postservice.model.cache.FeedCache;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.collections.DefaultRedisZSet;
import org.springframework.data.redis.support.collections.RedisZSet;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${feed.max-post-size}")
    private int maxPostSizeInFeed;

    @KafkaListener(topics = "${spring.data.kafka.topics.post_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(PostCache postCache, Acknowledgment acknowledgment) {
        postCache.getFollowersId().forEach(follower -> {
            Optional<FeedCache> feed = redisFeedRepository.findById(follower);
            if (feed.isPresent()) {
                RedisZSet<Long> ids = feed.get().getPostsId();
                if (ids.size() >= maxPostSizeInFeed) {
                    ids.reverseRange(ids.size() - 1, ids.size() - 1);
                }
                addNewPostIdInFeed(feed, postCache);
            } else {
                FeedCache newFeed = new FeedCache();

                newFeed.setId(follower);
                newFeed.getPostsId().add(postCache.getId());
                redisFeedRepository.save(newFeed);
            }
        });
        acknowledgment.acknowledge();
    }

    private void addNewPostIdInFeed(Optional<FeedCache> feed, PostCache postCache) {
        RedisZSet<Long> ids = feed.get().getPostsId();
        if (ids.size() >= maxPostSizeInFeed) {
            ids.reverseRange(ids.size() - 1, ids.size() - 1);
            Double minScore = ids.rangeWithScores(0, 0).stream()
                    .findFirst()
                    .map(ZSetOperations.TypedTuple::getScore)
                    .orElse(0.0);
            ids.addIfAbsent(postCache.getId(), minScore - 1);
        }
    }
}
