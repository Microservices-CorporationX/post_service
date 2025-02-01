package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.kafka.KafkaPostProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import feign.Request;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Mock
    private KafkaPostProducer kafkaPostProducer;

    @InjectMocks
    private PostService postService;
    @Mock
    private OrthographyService orthographyService;
    @Mock
    private PostViewEventPublisher postViewEventPublisher;
    @Mock
    private UserContext userContext;

    private final PostDto postDtoForUser = new PostDto("Test", 1L, null, null);

    @Captor
    ArgumentCaptor<PostViewEvent> postViewEventArgumentCaptor;

    @Captor
    ArgumentCaptor<List<Post>> postsCaptor;

    @Test
    void createDraftPostByUserSuccessTest() {
        Post postEntity = new Post();
        postEntity.setId(1L);

        when(postMapper.toEntity(postDtoForUser)).thenReturn(postEntity);
        when(postRepository.save(postEntity)).thenReturn(postEntity);

        Long result = postService.createDraftPost(postDtoForUser);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(postMapper).toEntity(postDtoForUser);
        verify(postRepository).save(postEntity);
    }

    @Test
    void createDraftPostByUserNotFoundFailTest() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost:8080/api/users/1",
                Map.of(),
                null,
                null,
                null
        );
        when(userServiceClient.getUser(anyLong()))
                .thenThrow(new FeignException.NotFound("User not found", request, null, null));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.createDraftPost(postDtoForUser);
        });
        assertTrue(exception.getMessage().contains("User id:"));

        verify(userServiceClient).getUser(anyLong());
    }

    @Test
    void publishPostSuccessTest() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);
        post.setDeleted(false);
        post.setAuthorId(1L);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDtoForUser);

        PostDto result = postService.publishPost(postId);

        assertNotNull(result);
        assertEquals(postDtoForUser, result);

        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    void publishPostNotFoundFailTest() {
        Long nonExistingPostId = 100L;
        when(postRepository.findById(nonExistingPostId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            postService.publishPost(nonExistingPostId);
        });

        assertEquals("Post not found with ID: " + nonExistingPostId, exception.getMessage());
        verify(postRepository).findById(nonExistingPostId);
        verifyNoInteractions(postMapper);
    }

    @Test
    void publishPostPublishedFailTest() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(true);
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.publishPost(postId);
        });

        assertTrue(exception.getMessage().contains("Post already published"));
        verify(postRepository).findById(postId);
        verifyNoInteractions(postMapper);
    }

    @Test
    void publishPostDeletedFailTest() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);
        post.setDeleted(true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.publishPost(postId);
        });

        assertTrue(exception.getMessage().contains("Post was deleted"));
        verify(postRepository).findById(postId);
        verifyNoInteractions(postMapper);
    }

    @Test
    void updatePostSuccessTest() {
        Long postId = 1L;
        String updatedContent = "Updated content";
        PostDto postDtoForUpdate = new PostDto(updatedContent, 1L, null, null);

        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setContent("Old content");

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setContent(updatedContent);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(updatedPost);
        when(postMapper.toDto(updatedPost)).thenReturn(postDtoForUpdate);

        PostDto result = postService.updatePost(postId, postDtoForUpdate);

        assertNotNull(result);
        assertEquals(updatedContent, result.content());
        verify(postRepository).findById(postId);
        verify(postRepository).save(existingPost);
        verify(postMapper).toDto(updatedPost);
    }

    @Test
    void updatePostNotFoundFailTest() {
        Long postId = 100L;
        String updatedContent = "Updated content";
        PostDto postDtoForUpdate = new PostDto(updatedContent, 1L, null, null);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            postService.updatePost(postId, postDtoForUpdate);
        });

        assertEquals("Post not found with ID: " + postId, exception.getMessage());
        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(postMapper);
    }

    @Test
    void deletePostSuccessTest() {
        Long postId = 1L;

        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setDeleted(false);

        Post deletedPost = new Post();
        deletedPost.setId(postId);
        deletedPost.setDeleted(true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(deletedPost);

        Long result = postService.deletePost(postId);

        assertNotNull(result);
        assertEquals(postId, result);
        assertTrue(deletedPost.isDeleted());
        verify(postRepository).findById(postId);
        verify(postRepository).save(existingPost);
    }

    @Test
    void deletePostNotFoundFailTest() {
        Long nonExistingPostId = 100L;

        when(postRepository.findById(nonExistingPostId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            postService.deletePost(nonExistingPostId);
        });

        assertEquals("Post not found with ID: " + nonExistingPostId, exception.getMessage());
        verify(postRepository).findById(nonExistingPostId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void deletePostDeletedFailTest() {
        Long postId = 1L;

        Post deletedPost = new Post();
        deletedPost.setId(postId);
        deletedPost.setDeleted(true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(deletedPost));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.deletePost(postId);
        });

        assertTrue(exception.getMessage().contains("Post with id: " + postId + " was deleted"));
        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void getPostSuccessTest() {
        Long postId = 1L;

        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setContent("Sample content");
        existingPost.setAuthorId(2L);

        PostDto postDto = new PostDto("Sample content", 1L, null, null);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postMapper.toDto(existingPost)).thenReturn(postDto);
        when(userContext.getUserId()).thenReturn(5L);

        PostDto result = postService.getPost(postId);

        assertNotNull(result);
        assertEquals(postId, result.userId());
        assertEquals("Sample content", result.content());
        verify(postRepository).findById(postId);
        verify(postMapper).toDto(existingPost);

        verify(postViewEventPublisher).publish(postViewEventArgumentCaptor.capture());
        PostViewEvent capturedPostViewEvent = postViewEventArgumentCaptor.getValue();
        assertEquals(postId, capturedPostViewEvent.getId());
        assertEquals(existingPost.getAuthorId(), capturedPostViewEvent.getAuthorId());
        assertEquals(userContext.getUserId(), capturedPostViewEvent.getUserId());
    }

    @Test
    void getPostNotFoundFailTest() {
        Long nonExistingPostId = 100L;

        when(postRepository.findById(nonExistingPostId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            postService.getPost(nonExistingPostId);
        });

        assertEquals("Post not found with ID: " + nonExistingPostId, exception.getMessage());
        verify(postRepository).findById(nonExistingPostId);
        verifyNoInteractions(postMapper);
    }

    @Test
    void getDraftPostsForUserSuccessTest() {
        Long userId = 1L;

        Post draftPost1 = new Post();
        draftPost1.setId(1L);
        draftPost1.setAuthorId(userId);
        draftPost1.setPublished(false);
        draftPost1.setDeleted(false);
        draftPost1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Post draftPost2 = new Post();
        draftPost2.setId(2L);
        draftPost2.setAuthorId(userId);
        draftPost2.setPublished(false);
        draftPost2.setDeleted(false);
        draftPost2.setCreatedAt(LocalDateTime.now());

        PostDto draftPostDto1 = new PostDto("Dto 1", 1L, null, null);
        PostDto draftPostDto2 = new PostDto("Dto 2", 2L, null, null);

        when(postRepository.findByAuthorId(userId)).thenReturn(List.of(draftPost1, draftPost2));
        when(postMapper.toDto(draftPost1)).thenReturn(draftPostDto1);
        when(postMapper.toDto(draftPost2)).thenReturn(draftPostDto2);

        List<PostDto> result = postService.getDraftPostsForUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).userId());
        assertEquals(1L, result.get(1).userId());
        verify(postRepository).findByAuthorId(userId);
        verify(postMapper).toDto(draftPost1);
        verify(postMapper).toDto(draftPost2);
    }

    @Test
    void getDraftPostsForUserNotDraftsSuccessTest() {
        long userId = 1L;

        when(postRepository.findByAuthorId(userId)).thenReturn(List.of());
        List<PostDto> result = postService.getDraftPostsForUser(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postRepository).findByAuthorId(userId);
        verifyNoInteractions(postMapper);
    }

    @Test
    void getDraftPostsForUserNotFoundFailTest() {
        long invalidUserId = 100L;
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost:8080/api/users" + invalidUserId,
                Map.of(),
                null,
                null,
                null
        );
        when(userServiceClient.getUser(anyLong()))
                .thenThrow(new FeignException.NotFound("User not found", request, null, null));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.createDraftPost(postDtoForUser);
        });

        assertTrue(exception.getMessage().contains("User id:"));
        verify(userServiceClient).getUser(anyLong());
    }

    @Test
    void checkGrammarPostContentAndChangeIfNeedSuccessTest() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setContent("Teh content");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setContent("Anothr contnt");

        List<Post> unpublishedPosts = Arrays.asList(post1, post2);

        when(postRepository.findAll()).thenReturn(unpublishedPosts);
        when(orthographyService.getCorrectContent("Teh content", 1L)).thenReturn("The content");
        when(orthographyService.getCorrectContent("Anothr contnt", 2L)).thenReturn("Another content");


        postService.checkGrammarPostContentAndChangeIfNeed();

        verify(postRepository).save(argThat(post ->
                post.getId().equals(1L) && "The content".equals(post.getContent())
        ));
        verify(postRepository).save(argThat(post ->
                post.getId().equals(2L) && "Another content".equals(post.getContent())
        ));

        verify(orthographyService).getCorrectContent("Teh content", 1L);
        verify(orthographyService).getCorrectContent("Anothr contnt", 2L);
        verify(postRepository).findAll();
        verifyNoMoreInteractions(postRepository, orthographyService);
    }

    @Test
    void testCheckGrammarPostContentAndChangeIfNeedExceptionNotFoundPublishedPostsFailTest() {
        when(postRepository.findAll()).thenReturn(Collections.emptyList());
        RuntimeException exception = assertThrows(EntityNotFoundException.class, postService::checkGrammarPostContentAndChangeIfNeed);
        assertEquals("The list of unpublished posts is null.", exception.getMessage());

        verify(postRepository).findAll();
    }

    @Test
    void publishScheduledPostsSuccessTest() {
        ReflectionTestUtils.setField(postService, "batchSize", 5);
        threadPoolTaskExecutor = Mockito.mock(ThreadPoolTaskExecutor.class);

        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        List<Post> posts = getPosts(userId, now);

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        postService.publishScheduledPosts();

        verify(postRepository, times(1)).findReadyToPublish();
    }

    @Test
    void publishBatchSuccessTest() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowOther = LocalDateTime.now();
        List<Post> posts = getPosts(userId, now);

        postService.publishBatch(posts, nowOther);

        verify(postRepository, times(1)).saveAll(postsCaptor.capture());
        assertThat(postsCaptor.getValue().size()).isEqualTo(posts.size());
        for (int i = 0; i < postsCaptor.getValue().size(); i++) {
            assertThat(postsCaptor.getValue().get(i).isPublished()).isEqualTo(posts.get(i).isPublished());
            assertThat(postsCaptor.getValue().get(i).getPublishedAt()).isEqualTo(nowOther);
        }
    }

    private List<Post> getPosts(Long userId, LocalDateTime now) {

        Post post1 = new Post();
        post1.setId(1L);
        post1.setAuthorId(userId);
        post1.setPublished(false);
        post1.setDeleted(false);
        post1.setCreatedAt(now.minusMinutes(5));

        Post post2 = new Post();
        post2.setId(2L);
        post2.setAuthorId(userId);
        post2.setPublished(false);
        post2.setDeleted(false);
        post2.setCreatedAt(now.minusMinutes(5));

        return List.of(post1, post2);
    }

}