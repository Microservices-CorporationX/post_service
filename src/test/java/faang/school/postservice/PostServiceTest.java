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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Spy
    private PostMapper postMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostUtil postUtil;

    private static PostCreatingRequest postCreatingRequest;
    private static PostResultResponse postResultResponse;

    @BeforeAll
    public static void setUp() {
        postCreatingRequest = PostCreatingRequest.builder()
                .id(1L)
                .content("This is a test content")
                .authorId(1L)
                .projectId(null)
                .published(false)
                .publishedAt(null)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postResultResponse = PostResultResponse.builder()
                .id(postCreatingRequest.authorId())
                .build();
    }

    @Test
    public void createPost_PostWasCreatedSuccessfully() {
        Post post = Post.builder()
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(false)
                .deleted(false)
                .build();

        Mockito.when(postUtil.validateCreator(postCreatingRequest.authorId(), postCreatingRequest.projectId()))
                .thenReturn(0);
        Mockito.when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResultResponse result = postService.createPost(postCreatingRequest);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, postResultResponse);
        verify(postUtil, times(1)).validateCreator(postCreatingRequest.authorId(), postCreatingRequest.projectId());
        verify(postRepository, times(1)).save(any(Post.class));
    }
}
