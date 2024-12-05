package faang.school.postservice.service.postService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostServiceValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    private final String POST_CONTENT = "postContent";
    private final long POST_ID = 3L;

    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private PostServiceValidator validator;

    @InjectMocks
    private PostServiceImpl postService;

    @Captor
    private ArgumentCaptor<Post> captor;

    @Test
    public void testCreateDraft() {
        long AUTHOR_ID = 1L;
        PostCreateDto postCreateDto = new PostCreateDto(POST_CONTENT, AUTHOR_ID, null);
        PostUpdateDto postUpdateDto = new PostUpdateDto(null, POST_CONTENT, AUTHOR_ID, null, null, null,
                false, false, null, null);
        Post savedPost = Post.builder().id(POST_ID).content(POST_CONTENT).authorId(AUTHOR_ID).published(false)
                .deleted(false).likes(null).comments(null).build();
        PostUpdateDto savedPostUpdateDto = new PostUpdateDto(POST_ID, POST_CONTENT, AUTHOR_ID, null, List.of(), List.of(),
                false, false, null, null);
        Post entity = postMapper.toEntity(postUpdateDto);
        when(postRepository.save(entity)).thenReturn(savedPost);

        PostUpdateDto result = postService.createDraft(postCreateDto);

        verify(postRepository, times(1)).save(entity);
        assertThat(result).isEqualTo(savedPostUpdateDto);
    }

    @Test
    public void testPublicPost() {
        Post post = Post.builder().id(POST_ID).content(POST_CONTENT).comments(List.of()).likes(List.of())
                .published(false).publishedAt(null).deleted(false).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(postRepository.save(any())).thenReturn(any());

        postService.publicPost(POST_ID);
        verify(postRepository, times(1)).findById(POST_ID);
        verify(postRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().isPublished()).isTrue();
    }

    @Test
    public void testUpdatePost() {
        PostUpdateDto newPost = new PostUpdateDto(POST_ID, "newContent", null, null, null,
                null, false, false, null, null);
        Post post = Post.builder().id(POST_ID).content(POST_CONTENT).comments(List.of()).likes(List.of())
                .published(false).publishedAt(null).deleted(false).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(postRepository.save(any())).thenReturn(any());

        postService.updatePost(newPost);

        verify(postRepository, times(1)).findById(POST_ID);
        verify(postRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getContent()).isEqualTo("newContent");
    }

    @Test
    public void testSoftDeletePost() {
        Post post = Post.builder().id(POST_ID).deleted(false).build();
        Post deletedPost = Post.builder().id(POST_ID).deleted(true).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(postRepository.save(deletedPost)).thenReturn(deletedPost);

        postService.softDeletePost(POST_ID);

        verify(postRepository, times(1)).findById(POST_ID);
        verify(postRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().isDeleted()).isTrue();
    }

    @Test
    public void testFilterAndSortPosts() {
        long authorId = 13L;
        Post firstPost = Post.builder().id(20L)
                .createdAt(LocalDateTime.of(2023, 11, 8, 0, 0, 0))
                .authorId(authorId).deleted(false).published(false).build();
        Post secondPost = Post.builder().id(21L)
                .createdAt(LocalDateTime.of(2022, 4, 12, 0, 0, 0))
                .authorId(authorId).deleted(false).published(false).build();
        Post thirdPost = Post.builder().id(22L)
                .createdAt(LocalDateTime.of(2024, 1, 22, 0, 0, 0))
                .authorId(authorId).deleted(false).published(false).build();
        Post fourthPost = Post.builder().id(23L)
                .createdAt(LocalDateTime.of(2024, 1, 22, 0, 0, 0))
                .authorId(authorId).deleted(false).published(true).build();
        Post fifthPost = Post.builder().id(24L)
                .createdAt(LocalDateTime.of(2024, 1, 22, 0, 0, 0))
                .authorId(authorId).deleted(true).published(false).build();
        when(postRepository.findByAuthorId(authorId)).thenReturn(List.of(firstPost, secondPost, thirdPost, fourthPost,
                fifthPost));

        List<PostUpdateDto> result = postService.getPostDraftsByAuthorId(authorId);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).id()).isEqualTo(22);
        assertThat(result.get(1).id()).isEqualTo(20);
        assertThat(result.get(2).id()).isEqualTo(21);
    }
}