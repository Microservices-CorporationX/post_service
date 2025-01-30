package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostForNewsFeedDto;
import faang.school.postservice.dto.user.UserForNewsFeedDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.message.event.CommentEvent;
import faang.school.postservice.message.event.LikeEvent;
import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.message.event.PostViewEvent;
import faang.school.postservice.message.producer.KafkaCommentPublisher;
import faang.school.postservice.message.producer.KafkaLikePublisher;
import faang.school.postservice.message.producer.KafkaPostPublisher;
import faang.school.postservice.message.producer.KafkaPostViewPublisher;
import faang.school.postservice.model.cache.CommentCache;
import faang.school.postservice.model.cache.Feed;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.cache.RedisFeedRepository;
import faang.school.postservice.repository.cache.RedisPostRepository;
import faang.school.postservice.repository.cache.RedisUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsFeedServiceTest {

    @InjectMocks
    private NewsFeedService newsFeedService;

    @Mock
    private PostService postService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private RedisFeedRepository redisFeedRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private UserMapper userMapper;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private RedisUserRepository redisUserRepository;

    @Mock
    private KafkaPostPublisher kafkaPostPublisher;

    @Mock
    private KafkaCommentPublisher kafkaCommentPublisher;

    @Mock
    private KafkaLikePublisher kafkaLikePublisher;

    @Mock
    private KafkaPostViewPublisher kafkaPostViewPublisher;

    @Mock
    private ExpirableLockRegistry redisLockRegistry;

    @Mock
    private TaskExecutor threadPool;

    private long userId;
    private long postId;
    private Feed feed;
    private PostCache postCache;
    private int maxCacheFeedSize;

    @BeforeEach
    public void setUp() {
        long viewCount = 5;
        long likeCount = 5;
        maxCacheFeedSize = 20;
        userId = 5L;
        postId = 5L;
        feed = new Feed(userId);
        postCache = PostCache.builder()
                .id(postId)
                .viewCount(viewCount)
                .likeCount(likeCount)
                .build();

        ReflectionTestUtils.setField(newsFeedService, "maxCacheFeedSize", maxCacheFeedSize);
    }

    @Test
    public void testGetFeedNoFeedReturnsEmptyList() {
        // arrange
        int maxFeedSize = 20;
        feed.addPostToFeed(postId, maxFeedSize);
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.empty());
        List<PostForNewsFeedDto> expected = new ArrayList<>();

        // act
        List<PostForNewsFeedDto> result = newsFeedService.getFeed(userId, null);

        // assert
        assertEquals(expected, result);
    }

    @Test
    public void testAddPostToCache() {
        // arrange
        PostDto postDto = PostDto.builder()
                .id(postId)
                .build();
        when(postMapper.toCachedPost(postDto)).thenReturn(postCache);

        // act
        newsFeedService.savePostToCache(postDto);

        // assert
        verify(redisPostRepository).save(postCache);
    }

    @Test
    public void testSavePostToCache() {
        // arrange
        PostDto postDto = PostDto.builder().build();
        PostCache postCache = new PostCache();
        when(postMapper.toCachedPost(postDto)).thenReturn(postCache);

        // act
        newsFeedService.savePostToCache(postDto);

        // assert
        verify(redisPostRepository).save(postCache);
    }

    @Test
    public void testAddPostToFollowerFeedInCache() {
        // arrange
        List<Long> followerIds = List.of(1L, 2L, 3L);
        PostEvent postEvent = new PostEvent(postId, followerIds);
        Lock lock = Mockito.mock(Lock.class);
        when(redisLockRegistry.obtain(any(String.class))).thenReturn(lock);

        // act
        newsFeedService.addPostToFollowersFeedInCache(postEvent);

        // assert
        verify(redisFeedRepository, times(3)).save(any(Feed.class));
    }

    @Test
    public void testAddPostToFollowerFeedInCacheCreatesNewFeed() {
        // arrange
        List<Long> followerIds = List.of(1L, 2L, 3L);
        PostEvent postEvent = new PostEvent(postId, followerIds);
        Lock lock = Mockito.mock(Lock.class);
        when(redisLockRegistry.obtain(any(String.class))).thenReturn(lock);

        // act
        newsFeedService.addPostToFollowersFeedInCache(postEvent);

        // assert
        verify(redisFeedRepository, times(3)).save(any(Feed.class));
    }

    @Test
    public void testSendCommentEvent() {
        // arrange
        CommentDto commentDto = CommentDto.builder()
                .postId(postId)
                .build();
        CommentEvent commentEvent = CommentEvent.builder()
                .postId(postId)
                .build();
        when(commentMapper.toCommentEvent(commentDto)).thenReturn(commentEvent);

        // act
        newsFeedService.sendCommentEventAsync(commentDto);

        // assert
        verify(kafkaCommentPublisher).publishCommentEvent(commentEvent);
    }

    @Test
    public void testAddCommentToPostCache() {
        // arrange
        long commentId = 5L;
        CommentEvent commentEvent = CommentEvent.builder()
                .id(commentId)
                .postId(postId)
                .build();
        CommentCache commentCache = CommentCache.builder()
                .id(commentId)
                .build();
        Lock lock = Mockito.mock(Lock.class);
        when(redisPostRepository.findById(postId)).thenReturn(Optional.of(postCache));
        when(redisLockRegistry.obtain(any(String.class))).thenReturn(lock);
        when(commentMapper.toCommentCache(commentEvent)).thenReturn(commentCache);

        // act
        newsFeedService.addCommentToPostCache(commentEvent);

        // assert
        verify(redisPostRepository).save(postCache);
    }

    @Test
    public void testSendLikeEvent() {
        // arrange
        LikePostDto likePostDto = LikePostDto.builder()
                .postId(postId)
                .build();
        LikeEvent likeEvent = new LikeEvent(postId);
        when(likeMapper.toLikeEvent(likePostDto)).thenReturn(likeEvent);

        // act
        newsFeedService.sendLikeEventAsync(likePostDto);

        // assert
        verify(kafkaLikePublisher).publishLikeEvent(likeEvent);
    }

    @Test
    public void testIncrementLikeCount() {
        // arrange
        LikeEvent likeEvent = new LikeEvent(postId);
        Lock lock = Mockito.mock(Lock.class);
        when(redisPostRepository.findById(postId)).thenReturn(Optional.of(postCache));
        when(redisLockRegistry.obtain(any(String.class))).thenReturn(lock);

        // act
        newsFeedService.incrementLikeCount(likeEvent);

        // assert
        verify(redisPostRepository).save(postCache);
    }

    @Test
    public void testSendPostViewEvent() {
        // arrange
        PostViewEvent postViewEvent = new PostViewEvent(postId);

        // act
        newsFeedService.sendPostViewEventAsync(postId);

        // assert
        verify(kafkaPostViewPublisher).publishPostViewEvent(postViewEvent);
    }

    @Test
    public void testIncrementPostViewCount() {
        // arrange
        Lock lock = Mockito.mock(Lock.class);
        PostViewEvent postViewEvent = new PostViewEvent(postId);
        when(redisPostRepository.findById(postId)).thenReturn(Optional.of(postCache));
        when(redisLockRegistry.obtain(any(String.class))).thenReturn(lock);

        // act
        newsFeedService.incrementPostViewCount(postViewEvent);

        // assert
        verify(redisPostRepository).save(postCache);
    }

    @Test
    public void testSaveUserToCacheById() {
        // arrange
        UserForNewsFeedDto user = UserForNewsFeedDto.builder().build();
        UserCache userCache = new UserCache();
        when(userServiceClient.getUserForNewsFeed(userId)).thenReturn(user);
        when(userMapper.toUserCache(user)).thenReturn(userCache);

        // act
        newsFeedService.saveUserToCacheById(userId);

        // verify
        verify(redisUserRepository).save(userCache);
    }

    @Test
    public void testSaveUserToCache() {
        // arrange
        UserForNewsFeedDto user = UserForNewsFeedDto.builder().build();
        UserCache userCache = new UserCache();
        when(userMapper.toUserCache(user)).thenReturn(userCache);

        // act
        newsFeedService.saveUserToCache(user);

        // verify
        verify(redisUserRepository).save(userCache);
    }
}
