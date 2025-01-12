package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.KafkaFeedHeaterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UsersCacheMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CachePost;
import faang.school.postservice.model.redis.CacheUser;
import faang.school.postservice.publisher.KafkaFeedHeaterProducer;
import faang.school.postservice.repository.redis.CacheFeedRepository;
import faang.school.postservice.repository.redis.CachePostRepository;
import faang.school.postservice.repository.redis.CacheUsersRepository;
import faang.school.postservice.service.post.PostCacheService;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {
    private static final long COUNT_USERS = 10L;
    @Mock
    private PostCacheService postCacheService;
    @Mock
    private PostService postService;
    @Mock
    private CachePostRepository cachePostRepository;
    @Mock
    private CacheUsersRepository cacheUsersRepository;
    @Mock
    private KafkaFeedHeaterProducer kafkaFeedHeaterProducer;
    @Mock
    private UserCacheService userCacheService;
    @Mock
    private UsersCacheMapper usersCacheMapper;
    @Mock
    private UserServiceClient userClient;
    @Mock
    private PostMapper mapper;
    @Mock
    private CacheFeedRepository cacheFeedRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RMap<Object, Object> versionMap;
    @InjectMocks
    private FeedService service;

    private final Long userId = 1L;
    private final Long postId = 100L;
    private final String version = "version";
    private final int batchSize = 20;
    private final int maxFeedSize = 2;
    private int numberOfUsersOnPage = 3;
    private CacheUser cacheUser;
    private PostDto postDto;
    private Post post;
    private CachePost cachePost;
    private List<Long> subscriberIds;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "batchSize", batchSize);
        ReflectionTestUtils.setField(service, "version", version);
        ReflectionTestUtils.setField(service, "maxFeedSize", maxFeedSize);
        ReflectionTestUtils.setField(service, "numberOfUsersOnPage", numberOfUsersOnPage);
        cacheUser = new CacheUser();
        cacheUser.setId(userId);
        postDto = new PostDto();
        post = new Post();
        cachePost = new CachePost();
        cacheUser.setFolloweesIds(List.of(2L, 3L));
        subscriberIds = List.of(2L, 3L);
    }

    @Test
    void testGetFeedByUserIdWithNoFollowerPosts() {
        // Arrange
        when(cacheFeedRepository.find(userId)).thenReturn(Set.of());
        when(userCacheService.getCacheUser(userId)).thenReturn(Optional.of(cacheUser));
        when(postService.getPostsByAuthorIds(cacheUser.getFolloweesIds(), 1L, 20))
                .thenReturn(List.of(postDto));
        when(mapper.toEntity(postDto)).thenReturn(post);
        when(mapper.toCachePost(post)).thenReturn(cachePost);
        when(redissonClient.getMap(version)).thenReturn(versionMap);

        // Act
        List<PostDto> result = service.getFeedByUserId(userId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postService, times(1)).getPostsByAuthorIds(anyList(), eq(1L), eq(20));
        verify(cacheFeedRepository, times(1)).saveAll(eq(userId), anyList());
    }

    @Test
    void testGetFeedByUserIdWithNoFollowerPostsIfPostIdPresentAndPostIdsEmpty() {
        // Arrange
        when(cacheFeedRepository.find(userId)).thenReturn(Set.of());
        when(userCacheService.getCacheUser(userId)).thenReturn(Optional.of(cacheUser));
        when(postService.getPostsByAuthorIds(cacheUser.getFolloweesIds(), postId, 20))
                .thenReturn(List.of(postDto));
        when(mapper.toEntity(postDto)).thenReturn(post);
        when(mapper.toCachePost(post)).thenReturn(cachePost);
        when(redissonClient.getMap(version)).thenReturn(versionMap);

        // Act
        List<PostDto> result = service.getFeedByUserId(userId, postId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postService, times(1)).getPostsByAuthorIds(anyList(), eq(postId), eq(20));
        verify(cacheFeedRepository, times(1)).saveAll(eq(userId), anyList());
    }

    @Test
    void testGetFeedByUserIdWithNoFollowerPostsIfPostIdPresentAndPostIdsNotEmpty() {
        // Arrange
        when(cacheFeedRepository.find(userId)).thenReturn(Set.of(1L));
        when(userCacheService.getCacheUser(userId)).thenReturn(Optional.of(cacheUser));
        when(postService.getPostsByAuthorIds(cacheUser.getFolloweesIds(), postId, 20))
                .thenReturn(List.of(postDto));
        when(mapper.toEntity(postDto)).thenReturn(post);
        when(mapper.toCachePost(post)).thenReturn(cachePost);
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(cacheFeedRepository.getRank(userId, postId)).thenReturn(1L);

        // Act
        List<PostDto> result = service.getFeedByUserId(userId, postId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postService, times(1)).getPostsByAuthorIds(anyList(), eq(postId), eq(20));
        verify(cacheFeedRepository, times(1)).saveAll(eq(userId), anyList());
    }

    @Test
    void testGetFeedByUserIdWithFollowerPosts() {
        // Arrange
        List<Long> followerPostIds = List.of(101L);
        when(cacheFeedRepository.find(userId)).thenReturn(Set.of(101L));
        when(postCacheService.getPostCacheByIds(followerPostIds))
                .thenReturn(List.of(new CachePost()));
        when(mapper.toDto(any(CachePost.class))).thenReturn(new PostDto());
        when(cacheFeedRepository.getRange(userId, 0, batchSize - 1)).thenReturn(Set.of(101L));

        // Act
        List<PostDto> result = service.getFeedByUserId(userId);

        // Assert
        assertNotNull(result);
        verify(postCacheService, times(1)).getPostCacheByIds(eq(followerPostIds));
        verify(mapper, atLeastOnce()).toDto(any(CachePost.class));
    }

    @Test
    void testAddPostIdToAuthorSubscribers() {
        // Arrange
        when(cacheFeedRepository.find(anyLong())).thenReturn(Set.of());
        doNothing().when(cacheFeedRepository).add(anyLong(), anyLong());

        // Act
        service.addPostIdToAuthorSubscribers(postId, subscriberIds);

        // Assert
        verify(cacheFeedRepository, times(subscriberIds.size())).add(anyLong(), eq(postId));
    }

    @Test
    void testAddPostIdToAuthorSubscribersWhenMoreThenMaxFeedSize() {
        // Arrange
        Set<Long> postIds = new HashSet<>() {{
            add(101L);
            add(102L);
            add(103L);
        }};
        when(cacheFeedRepository.find(anyLong())).thenReturn(postIds);
        doNothing().when(cacheFeedRepository).add(anyLong(), anyLong());

        // Act
        service.addPostIdToAuthorSubscribers(postId, subscriberIds);

        // Assert
        verify(cacheFeedRepository, times(subscriberIds.size())).add(anyLong(), eq(postId));
    }

    @Test
    void shouldSendHeatEvents() {
        // Arrange
        when(userClient.getCountUser()).thenReturn(COUNT_USERS);
        Page<UserDto> userPage = new PageImpl<>(List.of(new UserDto()));
        when(userClient.getUsers(anyInt(), anyInt())).thenReturn(userPage);
        when(usersCacheMapper.toCacheUser(any(UserDto.class))).thenReturn(new CacheUser());

        // Act
        service.sendHeatEvents();

        // Assert
        verify(cacheUsersRepository, times(4)).saveAll(anyList());
        verify(kafkaFeedHeaterProducer, atLeastOnce()).publish(any(KafkaFeedHeaterDto.class));
    }
}