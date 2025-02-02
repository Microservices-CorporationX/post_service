package faang.school.postservice.kafka.consumers;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.news_feed_models.NewsFeedPost;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.kafka.kafka_events_dtos.FeedHeatKafkaEventDto;
import faang.school.postservice.service.news_feed_service.AuthorCacheService;
import faang.school.postservice.service.news_feed_service.NewsFeedService;
import faang.school.postservice.service.news_feed_service.PostCacheService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaFeedHeatEventConsumer {

    private final NewsFeedService newsFeedService;
    private final AuthorCacheService authorCacheService;
    private final PostCacheService postCacheService;
    private final PostService postService;
    private final UserContext userContext;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${cache.news_feed.prefix_name}")
    private String newsFeedPrefix;

    @KafkaListener(
            topics = "${spring.kafka.topics_names.feed_heat_topic}",
            groupId = "${spring.kafka.group_id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleFeedHeatEvent(FeedHeatKafkaEventDto heatEvent, Acknowledgment ack) {
        try {

            heatEvent.getUsersIds().forEach(this::heatFeedForUser);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing Kafka event [{}]: {}", heatEvent, e.getMessage(), e);
        }
    }

    private void heatFeedForUser(Long userId) {
        userContext.setUserId(userId);
        List<NewsFeedPost> completeUserFeed = newsFeedService.getFeed(userId, null);
        completeUserFeed.stream()
                .map(NewsFeedPost::getAuthorId)
                .forEach(authorCacheService::saveAuthorCache);

        String redisKey = newsFeedPrefix + userId;
        long timestamp = System.currentTimeMillis();

        completeUserFeed.forEach(post -> {
            NewsFeedPost cachedPost = postCacheService.getPostCacheByPostId(post.getPostId());
            if (cachedPost == null) {
                log.info("Post {} not found in cache, fetching from DB...", post.getPostId());
                PostResponseDto postResponseDto = postService.getPost(post.getPostId());
                postCacheService.savePostCache(postResponseDto);
                cachedPost = postCacheService.getPostCacheByPostId(post.getPostId()); // Retrieve again after saving
            }

            if (cachedPost != null) {
                log.info("Adding post {} to news feed of user {}", cachedPost.getPostId(), userId);
                redisTemplate.opsForZSet().add(redisKey, String.valueOf(cachedPost.getPostId()), timestamp);
            }
        });

        log.info("Feed heat for user [{}] finished: {}", userId, completeUserFeed);
    }
}