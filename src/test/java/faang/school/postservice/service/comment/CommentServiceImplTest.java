package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.repository.UserCacheRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private CommentServiceValidator validator;

    @Mock
    private PostService postService;

    @Mock
    private UserCacheRepository userCacheRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void createCommentSuccess() {
        long userId = 1L;
        CommentDto commentDto = getCommentDto();
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userId);
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(userId);
        comment.setPost(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(postService.findPostById(anyLong())).thenReturn(Optional.of(post));

        CommentDto savedComment = commentService.createComment(commentDto);

        assertNotNull(savedComment);
        assertEquals(commentDto.getId(), savedComment.getId());
        verify(commentRepository, times(1)).save(comment);
        verify(userServiceClient, times(1)).getUserById(1L);
    }

    @Test
    void createCommentFailure_PostNotFound() {
        Mockito.lenient().when(postService.findPostById(getCommentDto().getPostId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> commentService.createComment(getCommentDto()));
        assertEquals("Post with id %s not found".formatted(getCommentDto().getPostId()), exception.getMessage());
    }

    @Test
    void updateCommentSuccess() {
        CommentDto commentDto = getCommentDto();
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(new Post());

        Mockito.lenient().when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(comment));

        commentService.updateComment(commentDto);

        Mockito.verify(commentRepository).save(comment);
    }

    @Test
    void updateCommentFailure_CommentNotFound() {
        Mockito.lenient().when(commentRepository.findById(getCommentDto().getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(getCommentDto()));
        assertEquals("Comment with id %s not found".formatted(getCommentDto().getId()), exception.getMessage());
    }

    @Test
    void getCommentsByPostIdSuccess() {
        List<Comment> comments = getComments().stream().sorted(Comparator.comparing(Comment::getCreatedAt).reversed()).toList();

        Mockito.lenient().when(commentRepository.findAllByPostId(Mockito.anyLong())).thenReturn(comments);

        assertEquals(getCommentDtoList().stream().sorted(Comparator.comparing(CommentDto::getCreatedAt).reversed()).peek(comment -> comment.setLikeIds(List.of())).toList(), commentService.getCommentsByPostId(getCommentDto().getPostId()));
    }

    @Test
    void deleteCommentSuccess() {
        commentService.deleteComment(getCommentDto().getId());
        Mockito.verify(validator).validateCommentId(getCommentDto().getId());
        Mockito.verify(commentRepository).deleteById(getCommentDto().getId());
    }

    private List<Comment> getComments() {
        return commentMapper.toEntity(getCommentDtoList()).stream()
                .peek(comment -> {
                    comment.setId(1L);
                    comment.setPost(Post.builder().id(1L).build());
                })
                .toList();
    }

    private List<CommentDto> getCommentDtoList() {
        return new ArrayList<>(List.of(getCommentDto(), getCommentDto(), getCommentDto(), getCommentDto(), getCommentDto()));
    }

    private CommentDto getCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .content("Content 1")
                .authorId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }
}