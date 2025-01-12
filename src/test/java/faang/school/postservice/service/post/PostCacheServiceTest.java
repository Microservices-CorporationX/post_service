package faang.school.postservice.service.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CachePost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.CachePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheServiceTest {
    @Mock
    private CachePostRepository cachePostRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper mapper;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RMap<Object, Object> versionMap;
    @Mock
    private RMap<String, Integer> vMap;
    @Mock
    private RLock rLock;
    @InjectMocks
    private PostCacheService service;

    private final String version = "version";
    private CachePost cachePost;
    private CachePost cachePostElse;
    private Post post;
    private List<Long> postIds;
    private CommentDto commentDtoOne;
    private CommentDto commentDtoTwo;

    @BeforeEach
    void setUp() {
        //Arrange
        ReflectionTestUtils.setField(service, "maxCommentsInPostCache", 3);
        ReflectionTestUtils.setField(service, "versionedKey", "versionedKey");
        ReflectionTestUtils.setField(service, "version", "version");
        commentDtoOne = CommentDto.builder()
                .id(1L)
                .postId(1L)
                .content("content")
                .build();
        commentDtoTwo = CommentDto.builder()
                .id(2L)
                .postId(1L)
                .content("contentTwo")
                .build();
        CopyOnWriteArraySet<CommentDto> comments = new CopyOnWriteArraySet<>();
        comments.add(commentDtoOne);
        List<Long> likesIds = new ArrayList<>();
        likesIds.add(1L);
        postIds = List.of(1L, 2L);
        cachePost = CachePost.builder()
                .id(1L)
                .numViews(1L)
                .numLikes(1L)
                .likeIds(likesIds)
                .comments(comments)
                .version(vMap)
                .build();
        cachePostElse = CachePost.builder()
                .id(2L)
                .build();
        post = Post.builder()
                .id(2L)
                .build();
        when(cachePostRepository.findById(anyLong())).thenReturn(Optional.of(cachePost));
    }

    @Test
    void testGetPostCacheByIdsSuccess() {
        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        service.getPostCacheByIds(postIds);

        //Assert
        verify(cachePostRepository, times(postIds.size())).save(any());
    }

    @Test
    void testGetPostCacheByIdsElseSuccess() {
        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        service.getPostCacheByIds(postIds);

        //Assert
        verify(cachePostRepository, times(postIds.size())).save(any());
    }

    @Test
    void testGetPostCacheByIdsException() {
        //Act
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Assert
        assertEquals(
                "There is no such post with id:1",
                assertThrows(
                        RuntimeException.class,
                        () -> service.getPostCacheByIds(postIds)).getMessage());
    }

    @Test
    void testIncrementPostLikes() {
        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        service.incrementPostLikes(1L, 2L);

        //Assert
        verify(cachePostRepository, times(2)).save(any());
        assertEquals(2L, cachePost.getNumLikes());
    }

    @Test
    void testAddPostView() {
        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        service.addPostView(1L);

        //Assert
        verify(cachePostRepository, times(2)).save(any());
        assertEquals(2L, cachePost.getNumViews());
    }

    @Test
    void testAddCommentToPostCacheSuccess() throws InterruptedException {
        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(vMap.getOrDefault(anyString(), any())).thenReturn(1);
        when(vMap.get(anyString())).thenReturn(1);
        service.addCommentToPostCache(1L, commentDtoTwo);

        //Assert
        verify(rLock).unlock();
        verify(cachePostRepository, times(2)).save(any());
        verify(vMap).put(anyString(), any());
    }

    @Test
    void testAddCommentToPostCacheLockNotAcquired() throws InterruptedException {
        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(false);

        //Assert
        assertEquals(
                "Could not acquire lock for post with id: 1",
                assertThrows(
                        RuntimeException.class,
                        () -> service.addCommentToPostCache(1L, commentDtoTwo)).getMessage());
    }

    @Test
    void testAddCommentToPostCacheVersionMismatch() throws InterruptedException {
        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(vMap.getOrDefault(anyString(), any())).thenReturn(1);
        when(vMap.get(anyString())).thenReturn(2);

        //Assert
        assertEquals(
                "The data was updated by another process.",
                assertThrows(
                        RuntimeException.class,
                        () -> service.addCommentToPostCache(1L, commentDtoTwo)).getMessage());
        verify(rLock).unlock();
    }

    @Test
    void testAddCommentToPostCacheInterrupted() throws InterruptedException {
        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenThrow(new InterruptedException());

        //Assert
        assertEquals(
                "Blocking error",
                assertThrows(
                        RuntimeException.class,
                        () -> service.addCommentToPostCache(1L, commentDtoTwo)).getMessage());
    }

    @Test
    void testAddCommentToPostCacheCheckCapacityFalse() throws InterruptedException {
        //Arrange
        CommentDto commentDtoTree = CommentDto.builder()
                .id(3L)
                .postId(1L)
                .content("contentTree")
                .build();
        CopyOnWriteArraySet<CommentDto> comments = new CopyOnWriteArraySet<>();
        comments.add(commentDtoOne);
        comments.add(commentDtoTwo);
        comments.add(commentDtoTree);
        cachePost.setComments(comments);

        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(vMap.getOrDefault(anyString(), any())).thenReturn(1);
        when(vMap.get(anyString())).thenReturn(1);
        service.addCommentToPostCache(1L, commentDtoTwo);

        //Assert
        verify(rLock).unlock();
        verify(cachePostRepository, times(2)).save(any());
        verify(vMap).put(anyString(), any());
    }

    @Test
    void testAddCommentToPostCacheIfCommentsNull() throws InterruptedException {
        //Arrange
        cachePost.setComments(null);

        //Act
        when(cachePostRepository.save(any())).thenReturn(cachePost);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(redissonClient.getMap(version)).thenReturn(versionMap);
        when(mapper.toCachePost(any())).thenReturn(cachePostElse);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(vMap.getOrDefault(anyString(), any())).thenReturn(1);
        when(vMap.get(anyString())).thenReturn(1);
        service.addCommentToPostCache(1L, commentDtoTwo);

        //Assert
        verify(rLock).unlock();
        verify(cachePostRepository, times(2)).save(any());
        verify(vMap).put(anyString(), any());
    }
}