package faang.school.postservice.service.news_feed_service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.news_feed_models.NewsFeedPost;
import faang.school.postservice.kafka.kafka_events_dtos.FeedHeatKafkaEventDto;
import faang.school.postservice.kafka.kafka_events_dtos.PostKafkaEventDto;
import faang.school.postservice.kafka.publishers.KafkaFeedHeatEventPublisher;
import faang.school.postservice.kafka.publishers.KafkaPostViewEventPublisher;
import faang.school.postservice.mapper.post.NewsFeedPostMapper;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.util.ListSplitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsFeedServiceTest {

    @Mock
    private PostService postService;

    @Mock
    private PostCacheService postCacheService;

    @Mock
    private NewsFeedPostMapper newsFeedPostMapper;

    @Mock
    private KafkaPostViewEventPublisher kafkaPostEventViewPublisher;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ListSplitter listSplitter;

    @Mock
    private KafkaFeedHeatEventPublisher kafkaFeedHeatEventPublisher;

    @InjectMocks
    private NewsFeedService newsFeedService;

    private static final int MAX_POSTS_IN_FEED = 500;
    private static final int PAGE_SIZE = 20;

    @BeforeEach
    void setup() {
        newsFeedService.setHeaterBatchSize(5);
        newsFeedService.setNewsFeedPrefix("newsFeed_");
        newsFeedService.setPageSize(PAGE_SIZE);
        newsFeedService.setMaxPostsAmountInCacheFeed(MAX_POSTS_IN_FEED);
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void heatFeed_ShouldFetchUsersAndSendBatchEvents() {
        List<Long> userIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        List<List<Long>> batchedUserIds = List.of(
                List.of(1L, 2L, 3L, 4L, 5L),
                List.of(6L, 7L, 8L, 9L, 10L)
        );

        when(userServiceClient.getAllUserIds()).thenReturn(userIds);
        when(listSplitter.split(userIds, 5)).thenReturn(batchedUserIds);

        newsFeedService.heatFeed();

        verify(userServiceClient, times(1)).getAllUserIds();
        verify(listSplitter, times(1)).split(userIds, 5);
        verify(kafkaFeedHeatEventPublisher, times(2)).sendFeedHeatingEvent(any(FeedHeatKafkaEventDto.class));
    }

    @Test
    void heatFeed_ShouldHandleEmptyUserList() {
        List<Long> emptyUserIds = List.of();
        when(userServiceClient.getAllUserIds()).thenReturn(emptyUserIds);

        newsFeedService.heatFeed();

        verify(userServiceClient, times(1)).getAllUserIds();
        verify(listSplitter, never()).split(any(), anyInt());
        verify(kafkaFeedHeatEventPublisher, never()).sendFeedHeatingEvent(any(FeedHeatKafkaEventDto.class));
    }

    @Test
    void addPostToNewsFeed_ShouldAddPostToRedisAndLimitFeedSize() {
        PostKafkaEventDto postEventDto = new PostKafkaEventDto(1L, List.of(2L, 3L), 10L);
        Long followerId = 2L;
        String redisKey = "newsFeed_" + followerId;
        String postId = postEventDto.getPostId().toString();

        long fakeTimestamp = System.currentTimeMillis();

        when(zSetOperations.add(eq(redisKey), eq(postId), anyDouble())).thenReturn(true);

        newsFeedService.addPostToNewsFeed(postEventDto, followerId);

        ArgumentCaptor<Double> timestampCaptor = ArgumentCaptor.forClass(Double.class);
        verify(zSetOperations).add(eq(redisKey), eq(postId), timestampCaptor.capture());

        double actualTimestamp = timestampCaptor.getValue();
        assertTrue(Math.abs(actualTimestamp - fakeTimestamp) < 10);

        verify(zSetOperations).removeRange(eq(redisKey), eq(0L), eq((long) -MAX_POSTS_IN_FEED - 1));
    }

    @Test
    void getFeed_ShouldFetchPostsFromRedis_WhenLastViewedPostIsNull() {
        Long userId = 1L;
        String redisKey = "newsFeed_" + userId;
        Set<String> postIds = new HashSet<>(Arrays.asList("101", "102", "103"));
        List<NewsFeedPost> mockPosts = List.of(
                NewsFeedPost.builder().postId(101L).content("Post 1").authorId(1L).authorName("User 1").build(),
                NewsFeedPost.builder().postId(102L).content("Post 2").authorId(1L).authorName("User 1").build(),
                NewsFeedPost.builder().postId(103L).content("Post 3").authorId(1L).authorName("User 1").build()
        );

        when(zSetOperations.reverseRange(eq(redisKey), eq(0L), eq((long) PAGE_SIZE - 1)))
                .thenReturn(postIds);

        when(postCacheService.getPostCacheByPostId(anyLong()))
                .thenAnswer(invocation -> {
                    Long postId = invocation.getArgument(0);
                    return mockPosts.stream().filter(p -> p.getPostId().equals(postId)).findFirst().orElse(null);
                });

        List<NewsFeedPost> result = newsFeedService.getFeed(userId, null);

        assertNotNull(result);
        assertEquals(3, result.size());

        verify(zSetOperations).reverseRange(eq(redisKey), eq(0L), eq((long) PAGE_SIZE - 1));
        verify(postCacheService, times(3)).getPostCacheByPostId(anyLong());
    }

    @Test
    void getFeed_ShouldFetchPostsAfterLastViewed_WhenLastViewedPostIsProvided() {
        Long userId = 1L;
        Long lastViewedPostId = 100L;
        String redisKey = "newsFeed_" + userId;
        Set<String> postIds = Set.of("101", "102", "103");

        when(redisTemplate.opsForZSet().rank(redisKey, lastViewedPostId.toString())).thenReturn(2L);

        when(redisTemplate.opsForZSet().reverseRange(eq(redisKey), eq(0L), eq(1L))).thenReturn(postIds);

        when(postCacheService.getPostCacheByPostId(anyLong()))
                .thenAnswer(invocation -> NewsFeedPost.builder()
                        .postId(invocation.getArgument(0))
                        .content("Mocked Content")
                        .authorId(1L)
                        .authorName("Mocked Author")
                        .build());

        List<NewsFeedPost> result = newsFeedService.getFeed(userId, lastViewedPostId);

        assertNotNull(result);
        assertEquals(3, result.size());

        verify(redisTemplate.opsForZSet()).rank(redisKey, lastViewedPostId.toString());
        verify(redisTemplate.opsForZSet()).reverseRange(redisKey, 0L, 1L);
    }
}