package faang.school.postservice.service.cache;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.kafka_events_dtos.PostKafkaEventDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostCacheMapper;
import faang.school.postservice.mapper.user.AuthorCacheMapper;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {

    private final AuthorCacheService authorCacheService;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final AuthorCacheMapper authorCacheMapper;
    private final PostCacheMapper postCacheMapper;
    private final PostCacheService postCacheService;
    @Value(value = "${cache.news_feed.max_posts_amount:500}")
    private int maxPostsAmountInCacheFeed;
    @Value("${cache.news_feed.prefix_name}")
    private String newsFeedPrefix;
    @Value(value = "${cache.news_feed.page_size:20}")
    private int pageSize;
    private final RedisTemplate<String, String> redisTemplate;

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

    public List<PostResponseDto> getFeed(Long userId, Long lastViewedPostId) {
        String redisKey = newsFeedPrefix + userId;
        Set<String> postIds;
        if (lastViewedPostId == null) {
            postIds = redisTemplate.opsForZSet().reverseRange(redisKey, 0, pageSize - 1);
        } else {
            postIds = getPostsAfterLastViewed(redisKey, lastViewedPostId);
        }
        List<PostResponseDto> fullPostsBatch = getFullPostsBatch(userId, postIds);

        handlePostViews(fullPostsBatch);
        return fullPostsBatch;
    }

    private Set<String> getPostsAfterLastViewed(String redisKey, Long lastViewedPostId) {
        Long rank = redisTemplate.opsForZSet().rank(redisKey, lastViewedPostId.toString());
        if (rank == null) {
            return new HashSet<>();
        }
        return redisTemplate.opsForZSet().reverseRange(redisKey, 0, rank - 1);
    }

    private void handlePostViews(List<PostResponseDto> readyToViewFeed) {
        readyToViewFeed.stream()
                .map(post -> new PostViewEventDto(post.getId()))
                .forEach(postViewEventProducer::handleNewPostView);
    }

    private List<PostResponseDto> getFullPostsBatch(Long userId, Set<String> postIds) {
        List<Long> postIdList = postIds.stream().map(Long::parseLong).toList();

        List<PostResponseDto> fullPostsBatch = new ArrayList<>(
                postIdList.stream()
                        .map(this::getPostDto)
                        .toList()
        );

        int feedLack = pageSize - fullPostsBatch.size();
        if (feedLack > 0) {
            Optional<Long> postPointerId = fullPostsBatch.isEmpty()
                    ? Optional.empty()
                    : Optional.of(fullPostsBatch.get(fullPostsBatch.size() - 1).getId());
            fullPostsBatch.addAll(postService.getFeedForUser(userId, feedLack, postPointerId));
        }
        return fullPostsBatch;
    }

    private UserDto getUserDto(Long userId) {
        return Optional.of(authorCacheMapper.toUserDto(authorCacheService.getAuthorCacheById(userId)))
                .orElseGet(() -> userServiceClient.getUser(userId));
    }

    private PostResponseDto getPostDto(Long postId) {
        return Optional.of(postCacheMapper.toResponseDto(postCacheService.getPostCacheByPostId(postId)))
                .orElseGet(() -> postService.getPost(postId));
    }
}