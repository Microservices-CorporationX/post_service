package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.ResourceService;
import faang.school.postservice.service.image.ImageResizeService;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostValidator postValidator;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private ResourceService resourceService;
    @Mock
    private ImageResizeService imageResizeService;
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
    void testAddPicturesOk() {
        Mockito.when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        Mockito.doNothing().when(postValidator).validateMedia(any(), any());
        Mockito.when(imageResizeService.resizeAndConvert(any(), anyInt(), anyInt())).thenReturn(new byte[1]);
        Mockito.when(resourceService.uploadResources(any(), any())).thenReturn(List.of(
                ResourceDto.builder().build(),
                ResourceDto.builder().build()
        ));

        List<ResourceDto> result = postService.addPictures(1, new MultipartFile[]{
                new MockMultipartFile("file1", new byte[0]),
                new MockMultipartFile("file2", new byte[0])}
        );

        assertEquals(2, result.size());
        Mockito.verify(imageResizeService, times(2)).resizeAndConvert(any(), anyInt(), anyInt());
        Mockito.verify(resourceService, times(1)).uploadResources(any(), any());

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
