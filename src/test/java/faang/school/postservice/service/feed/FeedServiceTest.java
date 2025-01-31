package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.FeedResponse;
import faang.school.postservice.dto.feed.PostDTO;
import faang.school.postservice.dto.post.PostVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.entities.CommentCache;
import faang.school.postservice.redis.entities.PostCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.service.cash.CommentCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private PostCacheRepository postCacheRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentCacheService commentCacheService;
    @Mock
    private UserFeedZSetService userFeedZSetService;

    @InjectMocks
    private FeedService feedService;

    private Post testPost;
    private PostCache testPostCache;
    private CommentCache testComment;

    @BeforeEach
    void setUp() {
        testPost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Test content")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .publishedAt(LocalDateTime.now())
                .verified(true)
                .likesCount(10L)
                .commentsCount(5L)
                .build();

        testPostCache = PostCache.builder()
                .id(1L)
                .authorId(1L)
                .content("Test content")
                .updatedAt(LocalDateTime.now())
                .publishedAt(LocalDateTime.now())
                .verified(true)
                .visibility(PostVisibility.PUBLIC)
                .likesCount(10L)
                .commentsCount(5L)
                .build();

        testComment = CommentCache.builder()
                .id(1L)
                .content("Test comment")
                .authorId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Get feed with valid input returns expected feed response")
    void getFeed_WithValidInput_ReturnsExpectedFeedResponse() {
        Long userId = 1L;
        Long lastPostId = null;
        int pageSize = 10;
        List<Long> postIds = Arrays.asList(1L, 2L);
        when(userFeedZSetService.getFeedPosts(userId, lastPostId, pageSize))
                .thenReturn(postIds);
        when(postCacheRepository.findById(1L))
                .thenReturn(Optional.of(testPostCache));
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(testPost));
        FeedResponse response = feedService.getFeed(userId, lastPostId, pageSize);
        assertNotNull(response);
        assertFalse(response.getPosts().isEmpty());
        assertEquals(1L, response.getPosts().get(0).getId());
    }

    @Test
    @DisplayName("Get feed with empty cache loads from database")
    void getFeed_WithEmptyCache_LoadsFromDatabase() {
        Long userId = 1L;
        Long lastPostId = null;
        int pageSize = 10;
        when(userFeedZSetService.getFeedPosts(userId, lastPostId, pageSize))
                .thenReturn(List.of())
                .thenReturn(List.of(1L));
        when(postRepository.findLatestPostsForUser(eq(userId), any(PageRequest.class)))
                .thenReturn(List.of(testPost));
        when(postCacheRepository.findById(1L))
                .thenReturn(Optional.of(testPostCache));
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(testPost));
        FeedResponse response = feedService.getFeed(userId, lastPostId, pageSize);
        assertNotNull(response);
        assertFalse(response.getPosts().isEmpty());
        verify(postRepository).findLatestPostsForUser(eq(userId), any(PageRequest.class));
    }

    @Test
    @DisplayName("Get feed with large page size limits to maximum page size")
    void getFeed_WithLargePageSize_LimitsToMaxPageSize() {
        Long userId = 1L;
        Long lastPostId = null;
        int pageSize = 100;
        when(userFeedZSetService.getFeedPosts(userId, lastPostId, 20))
                .thenReturn(List.of(1L));
        when(postCacheRepository.findById(1L))
                .thenReturn(Optional.of(testPostCache));
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(testPost));
        FeedResponse response = feedService.getFeed(userId, lastPostId, pageSize);
        assertNotNull(response);
        verify(userFeedZSetService).getFeedPosts(userId, lastPostId, 20);
    }

    @Test
    @DisplayName("Get post from cache returns cached version")
    void getPost_WithCachedPost_ReturnsCachedVersion() {
        Long postId = 1L;
        when(postCacheRepository.findById(postId))
                .thenReturn(Optional.of(testPostCache));
        when(postRepository.findById(postId))
                .thenReturn(Optional.of(testPost));
        Optional<PostDTO> result = ReflectionTestUtils.invokeMethod(
                feedService,
                "getPost",
                postId
        );
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(postId, result.get().getId());
        verify(postRepository, times(1)).findById(postId);
        verify(postCacheRepository, times(1)).findById(postId);
    }

    @Test
    @DisplayName("Get post without cache loads, caches, and returns post")
    void getPost_WithUncachedPost_CachesAndReturnsPost() {
        Long postId = 1L;
        LinkedHashSet<CommentCache> comments = new LinkedHashSet<>();
        comments.add(testComment);
        when(postCacheRepository.findById(postId))
                .thenReturn(Optional.empty());
        when(postRepository.findById(postId))
                .thenReturn(Optional.of(testPost));
        when(commentCacheService.fetchLatestComments(postId))
                .thenReturn(comments);
        when(postCacheRepository.save(any(PostCache.class)))
                .thenReturn(testPostCache);
        Optional<PostDTO> result = ReflectionTestUtils.invokeMethod(
                feedService,
                "getPost",
                postId
        );
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(postId, result.get().getId());
        verify(commentCacheService).fetchLatestComments(postId);
        verify(postCacheRepository).save(any(PostCache.class));
    }

    @Test
    @DisplayName("Map comments with null comments returns empty list")
    void mapComments_WithNullComments_ReturnsEmptyList() {
        testPostCache.setLastComments(null);
        List<CommentDto> result = ReflectionTestUtils.invokeMethod(
                feedService,
                "mapComments",
                testPostCache.getLastComments()
        );
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Map comments with valid comments returns comment DTO list")
    void mapComments_WithValidComments_ReturnsCommentDtoList() {
        LinkedHashSet<CommentCache> comments = new LinkedHashSet<>();
        comments.add(testComment);
        testPostCache.setLastComments(comments);
        List<CommentDto> result = ReflectionTestUtils.invokeMethod(
                feedService,
                "mapComments",
                testPostCache.getLastComments()
        );
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testComment.getId(), result.get(0).getId());
        assertEquals(testComment.getContent(), result.get(0).getContent());
    }
}