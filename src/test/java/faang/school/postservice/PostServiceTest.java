package faang.school.postservice;

import faang.school.postservice.dto.posts.PostCreatingRequest;
import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utils.PostUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostUtil postUtil;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    private static PostCreatingRequest postCreatingRequest;
    private static PostResultResponse postResultResponse;

    @BeforeAll
    public static void setUp() {
        postCreatingRequest = PostCreatingRequest.builder()
                .id(1L)
                .content("This is a test content")
                .authorId(1L)
                .projectId(null)
                .build();

        postResultResponse = PostResultResponse.builder()
                .id(postCreatingRequest.authorId())
                .build();
    }

    @Test
    public void createPost_PostWasCreatedSuccessfully() {
        Post post = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(false)
                .deleted(false)
                .build();

        Mockito.when(postUtil.validateCreator(postCreatingRequest.authorId(), postCreatingRequest.projectId()))
                .thenReturn(0);
        Mockito.when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        PostResultResponse result = postService.createPost(postCreatingRequest);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, postResultResponse);
        verify(postUtil, times(1)).validateCreator(postCreatingRequest.authorId(), postCreatingRequest.projectId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void publishPost_PostSuccessfullyPublished() {
        Post post = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(false)
                .deleted(false)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResultResponse result = postService.publishPost(post.getId());

        Assertions.assertEquals(postResultResponse, result);
        Assertions.assertDoesNotThrow(() -> postUtil.checkId(post.getId()));
    }

    @Test
    @Disabled
    public void createPost_PostHadNotAuthorOrProject() {
        Post post = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(null)
                .projectId(null)
                .published(false)
                .deleted(false)
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> postUtil.validateCreator(
                null,
                null));
    }


}
