package faang.school.postservice.service;

import faang.school.postservice.client.PostCorrecterClient;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.postCorrecter.PostCorrecterRequest;
import faang.school.postservice.dto.postCorrecter.PostCorrecterResponse;
import faang.school.postservice.dto.postCorrecter.textGears.TextGearsRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostCorrecterClient postCorrecterClient;

    @InjectMocks
    private PostService postService;

    private static final String POST_VALIDATION_SIMULTANEOUS_USER_AND_PROJECT_IDS_MESSAGE = "A post can be created by a user or a project";
    private static final String USER_SERVICE_COMMUNICATION_FAILURE_MESSAGE = "Failed to communicate with User Service. Please try again later.";
    private static final String POST_VALIDATION_POST_DELETED_MESSAGE = "Post is already deleted with ID: ";
    private static final String POST_VALIDATION_INVALID_POST_CONTENT_MESSAGE = "Post content cannot be empty, post ID: ";

    @Test
    void testCreatePostDraftSuccessful() {
        PostDto postDtoInitial = createPostDto(null, "Post content", 1L, null, false);
        PostDto postDtoResult = createPostDto(1L, "Post content", 1L, null, false);
        Post postInitial = createPost(null, "Post content", 1L, null, false, false);
        Post postResult = createPost(1L, "Post content", 1L, null, false, false);
        UserDto userDto = createUserDto(1L, "JohnDoe", "johndoe@gd.com");

        when(userServiceClient.getUser(postDtoInitial.getAuthorId())).thenReturn(userDto);
        when(postMapper.toEntity(postDtoInitial)).thenReturn(postInitial);
        when(postRepository.save(postInitial)).thenReturn(postResult);
        when(postMapper.toDto(postResult)).thenReturn(postDtoResult);

        PostDto result = postService.createPostDraft(postDtoInitial);

        verify(userServiceClient, times(1)).getUser(postDtoInitial.getAuthorId());
        verify(postMapper, times(1)).toEntity(postDtoInitial);
        verify(postRepository, times(1)).save(postInitial);
        verify(postMapper, times(1)).toDto(postResult);

        assertEquals(postDtoResult, result);
    }

    @Test
    void testCreatePostDraftWithInvalidPostData() {
        PostDto postDtoInitial = createPostDto(null, "Post content", 1L, 1L, false);

        PostValidationException postValidationException = assertThrows(PostValidationException.class, () ->
                postService.createPostDraft(postDtoInitial));

        assertEquals(POST_VALIDATION_SIMULTANEOUS_USER_AND_PROJECT_IDS_MESSAGE, postValidationException.getMessage());
    }

    @Test
    void testCreatePostDraftWithFailedInterServiceCommunication() {
        PostDto postDtoInitial = createPostDto(null, "Post content", 1L, null, false);
        long userId = postDtoInitial.getAuthorId();

        when(userServiceClient.getUser(userId)).thenThrow(new ExternalServiceException("Failed to communicate with User Service. Please try again later."));

        ExternalServiceException externalServiceException = assertThrows(ExternalServiceException.class, () ->
                postService.createPostDraft(postDtoInitial));

        verify(userServiceClient, times(1)).getUser(userId);

        assertEquals(USER_SERVICE_COMMUNICATION_FAILURE_MESSAGE, externalServiceException.getMessage());
    }

    @Test
    void testPublishPostSuccessful() {
        long postId = 1L;
        Post postInitial = createPost(1L, "Post content", 1L, null, false, false);
        Post postResult = createPost(1L, "Post content", 1L, null, true, false);
        PostDto postDtoResult = createPostDto(1L, "Post content", 1L, null, true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(postInitial));
        when(postRepository.save(postInitial)).thenReturn(postResult);
        when(postMapper.toDto(postResult)).thenReturn(postDtoResult);

        PostDto result = postService.publishPost(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(postInitial);
        verify(postMapper, times(1)).toDto(postResult);

        assertEquals(postDtoResult, result);
    }

    @Test
    void testPublishPostWithInvalidPostState() {
        long postId = 1L;
        Post postInitial = createPost(1L, "Post content", 1L, null, false, true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(postInitial));

        PostValidationException postValidationException = assertThrows(PostValidationException.class, () ->
                postService.publishPost(postId));

        verify(postRepository, times(1)).findById(postId);

        assertEquals(POST_VALIDATION_POST_DELETED_MESSAGE + postId, postValidationException.getMessage());
    }

    @Test
    void testUpdatePostSuccessful() {
        long postId = 1L;
        PostDto postUpdateDto = createPostDto(null, "Updated post content", null, null, false);
        Post postInitial = createPost(1L, "Post content", 1L, null, false, false);
        Post postResult = createPost(1L, "Updated post content", 1L, null, false, false);
        PostDto postDtoResult = createPostDto(1L, "Updated post content", 1L, null, false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(postInitial));
        when(postRepository.save(postInitial)).thenReturn(postResult);
        when(postMapper.toDto(postResult)).thenReturn(postDtoResult);

        PostDto result = postService.updatePost(postId, postUpdateDto);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(postInitial);
        verify(postMapper, times(1)).toDto(postResult);

        assertEquals(postDtoResult, result);
    }

    @Test
    void testUpdatePostWithInvalidPostContent() {
        long postId = 1L;
        PostDto postUpdateDto = createPostDto(null, "    ", null, null, false);
        Post postInitial = createPost(1L, "Post content", 1L, null, false, false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(postInitial));

        PostValidationException postValidationException = assertThrows(PostValidationException.class, () ->
                postService.updatePost(postId, postUpdateDto));

        verify(postRepository, times(1)).findById(postId);

        assertEquals(POST_VALIDATION_INVALID_POST_CONTENT_MESSAGE + postId, postValidationException.getMessage());
    }

    @Test
    void testSoftDeleteSuccessful() {
        long postId = 1L;
        Post postInitial = createPost(1L, "Post content", 1L, null, true, false);
        Post postResult = createPost(1L, "Post content", 1L, null, true, true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(postInitial));
        when(postRepository.save(postInitial)).thenReturn(postResult);

        postService.softDelete(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(postInitial);

        assertTrue(postInitial.isDeleted());
    }

    @Test
    void testSoftDeletePostAlreadyDeleted() {
        long postId = 1L;
        Post postInitial = createPost(1L, "Post content", 1L, null, true, true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(postInitial));

        PostValidationException postValidationException = assertThrows(PostValidationException.class, () ->
                postService.softDelete(postId));

        verify(postRepository, times(1)).findById(postId);

        assertEquals(POST_VALIDATION_POST_DELETED_MESSAGE + postId, postValidationException.getMessage());
    }

    @Test
    void testGetPostByIdSuccessful() {
        long postId = 1L;
        Post post = createPost(1L, "Post content", 1L, null, true, false);
        PostDto postDtoResult = createPostDto(1L, "Post content", 1L, null, true);

        when(postRepository.findByIdAndDeletedFalse(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDtoResult);

        PostDto result = postService.getPostById(postId);

        verify(postRepository, times(1)).findByIdAndDeletedFalse(postId);
        verify(postMapper, times(1)).toDto(post);

        assertEquals(postDtoResult, result);
    }

    @Test
    void testGetAllPostDraftsByUserIdSuccessful() {
        long userId = 1L;
        Post firstPost = createPost(1L, "Post #1 content", 1L, null, false, false,
                LocalDateTime.of(2023, 11, 13, 10, 30), null);
        Post secondPost = createPost(2L, "Post #2 content", 1L, null, true, false,
                LocalDateTime.of(2023, 12, 13, 10, 30),
                LocalDateTime.of(2023, 12, 13, 10, 35));
        Post thirdPost = createPost(3L, "Post #3 content", 1L, null, false, false,
                LocalDateTime.of(2024, 10, 13, 11, 30), null);
        List<Post> posts = List.of(firstPost, secondPost, thirdPost);
        PostDto firstPostDto = createPostDto(1L, "Post #1 content", 1L, null, false,
                LocalDateTime.of(2023, 11, 13, 10, 30), null);
        PostDto thirdPostDto = createPostDto(3L, "Post #3 content", 1L, null, false,
                LocalDateTime.of(2024, 10, 13, 11, 30), null);
        List<PostDto> postDraftsResult = List.of(thirdPostDto, firstPostDto);

        when(postRepository.findByAuthorId(userId)).thenReturn(posts);
        when(postMapper.toDto(any(List.class))).thenReturn(postDraftsResult);

        List<PostDto> result = postService.getAllPostDraftsByUserId(userId);

        verify(postRepository, times(1)).findByAuthorId(userId);
        verify(postMapper, times(1)).toDto(any(List.class));

        assertEquals(postDraftsResult, result);
    }

    @Test
    void testGetAllPostDraftsByProjectIdSuccessful() {
        long projectId = 1L;
        Post firstPost = createPost(1L, "Post #1 content", null, 1L, false, false,
                LocalDateTime.of(2023, 11, 13, 10, 30), null);
        Post secondPost = createPost(2L, "Post #2 content", null, 1L, true, false,
                LocalDateTime.of(2023, 12, 13, 10, 30),
                LocalDateTime.of(2023, 12, 13, 10, 35));
        Post thirdPost = createPost(3L, "Post #3 content", null, 1L, false, false,
                LocalDateTime.of(2024, 10, 13, 11, 30), null);
        List<Post> posts = List.of(firstPost, secondPost, thirdPost);
        PostDto firstPostDto = createPostDto(1L, "Post #1 content", null, 1L, false,
                LocalDateTime.of(2023, 11, 13, 10, 30), null);
        PostDto thirdPostDto = createPostDto(3L, "Post #3 content", null, 1L, false,
                LocalDateTime.of(2024, 10, 13, 11, 30), null);
        List<PostDto> postDraftsResult = List.of(thirdPostDto, firstPostDto);

        when(postRepository.findByProjectId(projectId)).thenReturn(posts);
        when(postMapper.toDto(any(List.class))).thenReturn(postDraftsResult);

        List<PostDto> result = postService.getAllPostDraftsByProjectId(projectId);

        verify(postRepository, times(1)).findByProjectId(projectId);
        verify(postMapper, times(1)).toDto(any(List.class));

        assertEquals(postDraftsResult, result);
    }

    @Test
    void testGetAllPublishedPostsByUserIdSuccessful() {
        long userId = 1L;
        Post firstPost = createPost(1L, "Post #1 content", 1L, null, false, false,
                LocalDateTime.of(2023, 11, 13, 10, 30), null);
        Post secondPost = createPost(2L, "Post #2 content", 1L, null, true, false,
                LocalDateTime.of(2023, 12, 13, 10, 30),
                LocalDateTime.of(2023, 12, 13, 10, 35));
        Post thirdPost = createPost(3L, "Post #3 content", 1L, null, true, false,
                LocalDateTime.of(2024, 10, 13, 11, 30),
                LocalDateTime.of(2024, 10, 13, 11, 35));
        List<Post> posts = List.of(firstPost, secondPost, thirdPost);
        PostDto secondPostDto = createPostDto(2L, "Post #2 content", 1L, null, true,
                LocalDateTime.of(2023, 12, 13, 10, 30),
                LocalDateTime.of(2023, 12, 13, 10, 35));
        PostDto thirdPostDto = createPostDto(3L, "Post #3 content", 1L, null, true,
                LocalDateTime.of(2024, 10, 13, 11, 30),
                LocalDateTime.of(2024, 10, 13, 11, 35));
        List<PostDto> postsPublishedResult = List.of(thirdPostDto, secondPostDto);

        when(postRepository.findByAuthorId(userId)).thenReturn(posts);
        when(postMapper.toDto(secondPost)).thenReturn(secondPostDto);
        when(postMapper.toDto(thirdPost)).thenReturn(thirdPostDto);

        List<PostDto> result = postService.getAllPublishedPostsByUserId(userId);

        verify(postRepository, times(1)).findByAuthorId(userId);
        verify(postMapper, times(1)).toDto(secondPost);
        verify(postMapper, times(1)).toDto(thirdPost);

        assertEquals(postsPublishedResult, result);
    }

    @Test
    void testGetAllPublishedPostsByProjectIdSuccessful() {
        long projectId = 1L;
        Post firstPost = createPost(1L, "Post #1 content", null, 1L, false, false,
                LocalDateTime.of(2023, 11, 13, 10, 30), null);
        Post secondPost = createPost(2L, "Post #2 content", null, 1L, true, false,
                LocalDateTime.of(2023, 12, 13, 10, 30),
                LocalDateTime.of(2023, 12, 13, 10, 35));
        Post thirdPost = createPost(3L, "Post #3 content", null, 1L, true, false,
                LocalDateTime.of(2024, 10, 13, 11, 30),
                LocalDateTime.of(2024, 10, 13, 11, 35));
        List<Post> posts = List.of(firstPost, secondPost, thirdPost);
        PostDto secondPostDto = createPostDto(2L, "Post #2 content", null, 1L, true,
                LocalDateTime.of(2023, 12, 13, 10, 30),
                LocalDateTime.of(2023, 12, 13, 10, 35));
        PostDto thirdPostDto = createPostDto(3L, "Post #3 content", null, 1L, true,
                LocalDateTime.of(2024, 10, 13, 11, 30),
                LocalDateTime.of(2024, 10, 13, 11, 35));
        List<PostDto> postsPublishedResult = List.of(thirdPostDto, secondPostDto);

        when(postRepository.findByProjectId(projectId)).thenReturn(posts);
        when(postMapper.toDto(secondPost)).thenReturn(secondPostDto);
        when(postMapper.toDto(thirdPost)).thenReturn(thirdPostDto);

        List<PostDto> result = postService.getAllPublishedPostsByProjectId(projectId);

        verify(postRepository, times(1)).findByProjectId(projectId);
        verify(postMapper, times(1)).toDto(secondPost);
        verify(postMapper, times(1)).toDto(thirdPost);

        assertEquals(postsPublishedResult, result);
    }

    @Test
    public void testSendPostToSpellingCheck() {
        String testContent = "test content";
        Post mockPost = mock(Post.class);
        PostCorrecterResponse mockResponse = mock(PostCorrecterResponse.class);
        PostCorrecterRequest mockRequest = TextGearsRequest.builder()
                .text(mockPost.getContent())
                .build();

        when(postRepository.findReadyToPublish()).thenReturn(List.of(mockPost));
        when(postCorrecterClient.checkPost(mockRequest)).thenReturn(mockResponse);
        when(mockResponse.isSuccess()).thenReturn(true);
        when(mockResponse.getCorrectedPost()).thenReturn(testContent);

        PostService spy = spy(postService);
        spy.sendPostToSpellingCheck();

        verify(postRepository, times(1)).findReadyToPublish();
        verify(spy).correctAndSavePost(mockPost, mockRequest);
    }

    private PostDto createPostDto(Long id, String content, Long authorId, Long projectId, boolean published) {
        PostDto postDto = new PostDto();
        postDto.setId(id);
        postDto.setContent(content);
        postDto.setAuthorId(authorId);
        postDto.setProjectId(projectId);
        postDto.setPublished(published);
        return postDto;
    }

    private PostDto createPostDto(Long id, String content, Long authorId, Long projectId, boolean published,
                                  LocalDateTime createdAt, LocalDateTime publishedAt) {
        PostDto postDto = new PostDto();
        postDto.setId(id);
        postDto.setContent(content);
        postDto.setAuthorId(authorId);
        postDto.setProjectId(projectId);
        postDto.setPublished(published);
        postDto.setCreatedAt(createdAt);
        postDto.setPublishedAt(publishedAt);
        return postDto;
    }

    private Post createPost(Long id, String content, Long authorId, Long projectId, boolean published, boolean deleted) {
        Post post = new Post();
        post.setId(id);
        post.setContent(content);
        post.setAuthorId(authorId);
        post.setProjectId(projectId);
        post.setPublished(published);
        post.setDeleted(deleted);
        return post;
    }

    private Post createPost(Long id, String content, Long authorId, Long projectId, boolean published, boolean deleted,
                            LocalDateTime createdAt, LocalDateTime publishedAt) {
        Post post = new Post();
        post.setId(id);
        post.setContent(content);
        post.setAuthorId(authorId);
        post.setProjectId(projectId);
        post.setPublished(published);
        post.setDeleted(deleted);
        post.setCreatedAt(createdAt);
        post.setPublishedAt(publishedAt);
        return post;
    }

    private UserDto createUserDto(Long id, String username, String email) {
        return new UserDto(id, username, email);
    }
}
