package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.analytics.AnalyticsEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostViewEventMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.postview.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostValidator postValidator;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostViewEventPublisher postViewEventPublisher;
    @Mock
    private UserContext userContext;
    @Mock
    private PostViewEventMapper postViewEventMapper;
    @InjectMocks
    private PostService postService;

    @Test
    void testFindEntityByIdFounded() {
        long postId = 1L;
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));

        Post post = assertDoesNotThrow(() -> postService.findEntityById(postId));
        assertNotNull(post);
    }

    @Test
    void testFindEntityByIdNotFounded() {
        long postId = 1L;
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> postService.findEntityById(postId));
    }

    @Test
    void testCreateOk() {
        Mockito.doNothing().when(postValidator).validateCreation(any());
        Mockito.when(postMapper.toEntity(any())).thenReturn(new Post());
        Mockito.when(postRepository.save(any())).thenReturn(new Post());
        Mockito.when(postMapper.toDto(any())).thenReturn(PostDto.builder().build());

        PostDto postDto = PostDto.builder().id(1L).build();

        postService.create(postDto);
        Mockito.verify(postRepository, times(1)).save(any());
    }

    @Test
    void testCreateValidationError() {
        Mockito.doThrow(new DataValidationException("Error")).when(postValidator).validateCreation(any());
        PostDto postDto = PostDto.builder().build();

        assertThrows(DataValidationException.class, () -> postService.create(postDto));
        Mockito.verify(postRepository, times(0)).save(any());
    }

    @Test
    void testPublish() {
        Mockito.when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        Mockito.when(postRepository.save(any())).thenReturn(new Post());
        Mockito.when(postMapper.toDto(any())).thenReturn(PostDto.builder().build());

        PostDto post = assertDoesNotThrow(() -> postService.publish(1));
        assertNotNull(post);
    }

    @Test
    void testUpdateOk() {
        Mockito.when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        Mockito.doNothing().when(postValidator).validateUpdate(any(), any());
        Mockito.when(postRepository.save(any())).thenReturn(new Post());
        Mockito.when(postMapper.toDto(any())).thenReturn(PostDto.builder().build());

        PostDto postDto = PostDto.builder().id(1L).build();
        PostDto result = postService.update(postDto);

        assertNotNull(result);
        Mockito.verify(postRepository, times(1)).save(any());
    }

    @Test
    void testUpdateValidationError() {
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.of(new Post()));
        Mockito.doThrow(new DataValidationException("Error")).when(postValidator).validateUpdate(any(), any());

        PostDto postDto = PostDto.builder().id(1L).build();

        assertThrows(DataValidationException.class, () -> postService.update(postDto));
        Mockito.verify(postRepository, times(0)).save(any());
    }

    @Test
    void testDeleteOk() {
        Post postEntity = new Post();
        postEntity.setDeleted(false);
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.of(postEntity));
        Mockito.when(postRepository.save(any())).thenReturn(new Post());
        Mockito.when(postMapper.toDto(any())).thenReturn(PostDto.builder().build());

        PostDto result = postService.deletePost(1L);

        assertNotNull(result);
        Mockito.verify(postRepository, times(1)).save(any());
    }

    @Test
    void testDeleteAlreadyDeleted() {
        Post postEntity = new Post();
        postEntity.setDeleted(true);
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.of(postEntity));

        assertThrows(DataValidationException.class, () -> postService.deletePost(1L));
        Mockito.verify(postRepository, times(0)).save(any());
    }

    @Test
    void voidGetAllPublishedByAuthorId() {
        Mockito.when(postRepository.findByAuthorIdWithLikes(anyLong())).thenReturn(getPosts());
        Mockito.when(postMapper.toDto(any())).thenReturn(PostDto.builder().build());

        List<PostDto> result = postService.getAllPublishedByAuthorId(1);
        assertEquals(3, result.size());
    }

    @Test
    void voidGetAllNonPublishedByAuthorId() {
        Mockito.when(postRepository.findByAuthorIdWithLikes(anyLong())).thenReturn(getPosts());
        Mockito.when(postMapper.toDto(any())).thenReturn(PostDto.builder().build());

        List<PostDto> result = postService.getAllNonPublishedByAuthorId(1);
        assertEquals(2, result.size());
    }

    @Test
    void voidGetAllPublishedByProjectId() {
        Mockito.when(postRepository.findByProjectIdWithLikes(anyLong())).thenReturn(getPosts());
        Mockito.when(postMapper.toDto(any())).thenReturn(PostDto.builder().build());

        List<PostDto> result = postService.getAllPublishedByProjectId(1);
        assertEquals(3, result.size());
    }

    @Test
    void voidGetAllNonPublishedByProjectId() {
        Mockito.when(postRepository.findByProjectIdWithLikes(anyLong())).thenReturn(getPosts());
        Mockito.when(postMapper.toDto(any())).thenReturn(PostDto.builder().build());

        List<PostDto> result = postService.getAllNonPublishedByProjectId(1);
        assertEquals(2, result.size());
    }

    @Test
    void testPublishPostViewEventAfterGetAllPosts() {
        long userId = 1L;
        int numberOfInvocations = 3;
        when(postRepository.findByAuthorIdWithLikes(anyLong())).thenReturn(getPosts());
        when(postMapper.toDto(any())).thenReturn(PostDto.builder().build());
        when(userContext.getUserId()).thenReturn(userId);
        when(postViewEventMapper.toAnalyticsEventDto(any(), eq(userId))).thenReturn(new AnalyticsEventDto());

        postService.getAllPublishedByAuthorId(userId);

        verify(userContext).getUserId();
        verify(postViewEventMapper, times(numberOfInvocations)).toAnalyticsEventDto(any(), eq(userId));
        verify(postViewEventPublisher, times(numberOfInvocations)).publish(any());
    }

    private List<Post> getPosts() {
        return List.of(
                Post.builder().published(true).createdAt(LocalDateTime.now()).deleted(true).build(),
                Post.builder().published(true).createdAt(LocalDateTime.now().minusDays(3)).deleted(false).build(),
                Post.builder().published(true).createdAt(LocalDateTime.now().minusDays(1)).deleted(false).build(),
                Post.builder().published(true).createdAt(LocalDateTime.now().minusDays(2)).deleted(false).build(),

                Post.builder().published(false).createdAt(LocalDateTime.now()).deleted(true).build(),
                Post.builder().published(false).createdAt(LocalDateTime.now().minusDays(1)).deleted(false).build(),
                Post.builder().published(false).createdAt(LocalDateTime.now().minusDays(2)).deleted(false).build()
        );
    }
}
