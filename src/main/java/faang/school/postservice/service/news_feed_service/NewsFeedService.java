package faang.school.postservice.service.news_feed_service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.news_feed_models.NewsFeedPost;
import faang.school.postservice.kafka.kafka_events_dtos.FeedHeatKafkaEventDto;
import faang.school.postservice.kafka.kafka_events_dtos.PostKafkaEventDto;
import faang.school.postservice.kafka.kafka_events_dtos.PostViewKafkaEventDto;
import faang.school.postservice.kafka.publishers.KafkaFeedHeatEventPublisher;
import faang.school.postservice.kafka.publishers.KafkaPostViewEventPublisher;
import faang.school.postservice.mapper.post.NewsFeedPostMapper;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.util.ListSplitter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
@Setter
public class NewsFeedService {

    private final PostService postService;
    private final PostCacheService postCacheService;
    private final NewsFeedPostMapper newsFeedPostMapper;
    private final KafkaPostViewEventPublisher kafkaPostEventViewPublisher;
    private final KafkaFeedHeatEventPublisher kafkaFeedHeatEventPublisher;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserServiceClient userServiceClient;
    private final ListSplitter listSplitter;

    @Value(value = "${cache.news_feed.max_posts_amount:500}")
    private int maxPostsAmountInCacheFeed;
    @Value("${cache.news_feed.prefix_name}")
    private String newsFeedPrefix;
    @Value(value = "${cache.news_feed.page_size:20}")
    private int pageSize;
    @Value(value = "${cache.heater_batch_size}")
    private int heaterBatchSize;

    public void heatFeed() {
        List<Long> userIds = userServiceClient.getAllUserIds();
        if (userIds.isEmpty()) {
            log.warn("No users found for heating news feed. Skipping batch processing.");
            return;
        }
        List<List<Long>> batchedUserIds = listSplitter.split(userIds, heaterBatchSize);
        batchedUserIds.stream()
                .map(FeedHeatKafkaEventDto::new)
                .forEach(kafkaFeedHeatEventPublisher::sendFeedHeatingEvent);
    }

    public void addPostToNewsFeed(PostKafkaEventDto postEventDto, Long followerId) {
        String redisKey = newsFeedPrefix + followerId;
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

    public List<NewsFeedPost> getFeed(Long userId, Long lastViewedPostId) {
        String redisKey = newsFeedPrefix + userId;
        Set<String> postIds;
        if (lastViewedPostId == null) {
            postIds = Optional.ofNullable(redisTemplate.opsForZSet().reverseRange(redisKey, 0, pageSize - 1))
                    .orElse(Collections.emptySet());
        } else {
            postIds = Optional.ofNullable(getPostsAfterLastViewed(redisKey, lastViewedPostId))
                    .orElse(Collections.emptySet());
        }
        List<NewsFeedPost> fullPostsBatch = getFullPostsBatch(userId, postIds);

        handlePostViews(fullPostsBatch);
        return fullPostsBatch;
    }

    private Set<String> getPostsAfterLastViewed(String redisKey, Long lastViewedPostId) {
        Long lastViedPostPosition = redisTemplate.opsForZSet().rank(redisKey, lastViewedPostId.toString());
        if (lastViedPostPosition == null) {
            return new HashSet<>();
        }
        return redisTemplate.opsForZSet().reverseRange(redisKey, 0, lastViedPostPosition - 1);
    }

    private List<NewsFeedPost> getFullPostsBatch(Long userId, Set<String> postIds) {
        List<Long> postIdList = postIds.stream().map(Long::parseLong).toList();

        List<NewsFeedPost> fullPostsBatch = new ArrayList<>(
                postIdList.stream()
                        .map(this::getNewsFeedPost)
                        .toList()
        );

        int feedLack = pageSize - fullPostsBatch.size();
        if (feedLack > 0) {
            Optional<Long> postPointerId = fullPostsBatch.isEmpty()
                    ? Optional.empty()
                    : Optional.of(fullPostsBatch.get(fullPostsBatch.size() - 1).getPostId());
            List<NewsFeedPost> mappedPosts = postService.getFeedForUser(userId, feedLack, postPointerId)
                    .stream()
                    .map(newsFeedPostMapper::toCache)
                    .toList();
            fullPostsBatch.addAll(mappedPosts);
        }

        return fullPostsBatch;
    }

    private NewsFeedPost getNewsFeedPost(Long postId) {
        return Optional.ofNullable(postCacheService.getPostCacheByPostId(postId))
                .orElseGet(() -> newsFeedPostMapper.toCache(postService.getPost(postId)));
    }

    private void handlePostViews(List<NewsFeedPost> readyToViewFeed) {
        readyToViewFeed.stream()
                .map(post -> new PostViewKafkaEventDto(post.getPostId()))
                .forEach(kafkaPostEventViewPublisher::sendPostViewEvent);
    }
}