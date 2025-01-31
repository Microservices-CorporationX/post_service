package faang.school.postservice.service.cache;

import faang.school.postservice.dto.kafka_events.PostKafkaEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedCacheService {

    @Value(value = "${cache.news_feed.max_posts_amount:500}")
    private int maxPostsAmountInCacheFeed;
    private final String NEWS_FEED_PREFIX = "news_feed:";
    private final RedisTemplate<String, String> redisTemplate;

    public void addPostToNewsFeed(PostKafkaEventDto postEventDto, Long followerId) {
        String redisKey = NEWS_FEED_PREFIX + followerId;
        String postId = String.valueOf(postEventDto.getPostId());
        long timestamp = System.currentTimeMillis();
        try {
            redisTemplate.opsForZSet().add(redisKey, postId, timestamp);
            redisTemplate.opsForZSet().removeRange(redisKey, 0, -maxPostsAmountInCacheFeed - 1);
            log.info("Added post [{}] to news feed of user [{}]", postEventDto.getPostId(), followerId);
        } catch (Exception e) {
            log.error("Failed to update news feed for user [{}]: {}", followerId, e.getMessage(), e);
        }
    }
}