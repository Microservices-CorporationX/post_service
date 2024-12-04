package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import feign.Request;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @InjectMocks
    private PostService postService;
    @Mock
    private ResponseTextGears responseTextGears;

    private final PostDto postDtoForUser = new PostDto("Test", 1L, null);

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
        PostDto postDtoForUpdate = new PostDto(updatedContent, 1L, null);

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
        PostDto postDtoForUpdate = new PostDto(updatedContent, 1L, null);

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

        PostDto postDto = new PostDto("Sample content", 1L, null);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postMapper.toDto(existingPost)).thenReturn(postDto);

        PostDto result = postService.getPost(postId);

        assertNotNull(result);
        assertEquals(postId, result.userId());
        assertEquals("Sample content", result.content());
        verify(postRepository).findById(postId);
        verify(postMapper).toDto(existingPost);
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

        PostDto draftPostDto1 = new PostDto("Dto 1", 1L, null);
        PostDto draftPostDto2 = new PostDto("Dto 2", 2L, null);

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
    void testCheckGrammarPostContentAndChangeIfNeedSuccessTest() throws IOException, InterruptedException {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);
        post.setDeleted(false);
        post.setContent("Original content");

        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        List<Post> mockPosts = List.of(post);

        when(postRepository.findAll()).thenReturn(mockPosts);
        when(responseTextGears.getResponsesWithCorrectText("Original content")).thenReturn(mockResponse);

        PostService spyPostService = spy(postService);

        doReturn(true).when(spyPostService).extractBooleanSafely(mockResponse);
        doReturn("Corrected content").when(spyPostService).extractTextFromRequest(mockResponse);

        spyPostService.checkGrammarPostContentAndChangeIfNeed();

        verify(postRepository).save(post);
        assertEquals("Corrected content", post.getContent());
    }


    @Test
    void testCheckGrammarPostContentAndChangeIfNeedResponseStatusFalseFailTest() throws IOException, InterruptedException {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);
        post.setDeleted(false);
        post.setContent("Original content");

        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        List<Post> mockPosts = List.of(post);
        when(postRepository.findAll()).thenReturn(mockPosts);
        when(responseTextGears.getResponsesWithCorrectText("Original content")).thenReturn(mockResponse);
        PostService spyPostService = spy(postService);

        doReturn(false).when(spyPostService).extractBooleanSafely(mockResponse);
        spyPostService.checkGrammarPostContentAndChangeIfNeed();
        verify(postRepository, never()).save(any());
    }

    @Test
    void testCheckGrammarPostContentAndChangeIfNeedExceptionNotFoundPublishedPostsFailTest() {
        when(postRepository.findAll()).thenReturn(Collections.emptyList());
        RuntimeException exception = assertThrows(EntityNotFoundException.class, postService::checkGrammarPostContentAndChangeIfNeed);
        assertEquals("The list of unpublished posts is null.", exception.getMessage());

        verify(postRepository).findAll();
    }
}