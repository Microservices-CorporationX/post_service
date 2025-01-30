package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.kafka.KafkaServiceImpl;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private PostValidator postValidator;
    @Mock
    private UserValidator userValidator;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private KafkaServiceImpl kafkaServiceImpl;
    @InjectMocks
    private CommentService commentService;

    private final long commentId = 1L;
    private final long postId = 2L;
    private final long authorId = 3L;
    private final String content = "Content";

    @Test
    public void createCommentSuccessTest() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = getComment(now);
        CommentDto commentDto = getCommentDto(now);
        when(userContext.getUserId()).thenReturn(commentId);
        when(commentMapper.toEntity(any())).thenReturn(comment);
        when(postRepository.findById(any())).thenReturn(Optional.of(getPost()));
        when(commentRepository.save(any())).thenReturn(comment);
        when(commentMapper.toDto(any())).thenReturn(commentDto);
        doNothing().when(kafkaServiceImpl).sendCommentEvent(any());

        CommentDto actualResult = commentService.createComment(commentDto);

        verify(userContext, times(1)).getUserId();
        verify(commentMapper, times(1)).toEntity(any());
        verify(postRepository, times(1)).findById(any());
        verify(commentRepository, times(1)).save(any());
        verify(commentMapper, times(1)).toDto(any());
        verify(kafkaServiceImpl, times(1)).sendCommentEvent(any());
        assertThat(commentDto.getId()).isEqualTo(actualResult.getId());
        assertThat(commentDto.getAuthorId()).isEqualTo(actualResult.getAuthorId());
        assertThat(commentDto.getPostId()).isEqualTo(actualResult.getPostId());
        assertThat(commentDto.getContent()).isEqualTo(actualResult.getContent());
        assertThat(commentDto.getCreatedAt()).isEqualTo(actualResult.getCreatedAt());
        assertThat(commentDto.getUpdatedAt()).isEqualTo(actualResult.getUpdatedAt());
    }

    @Test
    public void createCommentWithExceptionFailTest() {

        LocalDateTime now = LocalDateTime.now();
        Comment comment = getComment(now);
        CommentDto commentDto = getCommentDto(now);
        when(userContext.getUserId()).thenReturn(commentId);
        doNothing().when(postValidator).validatePostExist(anyLong());
        when(commentMapper.toEntity(any())).thenReturn(comment);
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> commentService.createComment(commentDto)
                );

        verify(userContext, times(1)).getUserId();
        verify(commentMapper, times(1)).toEntity(any());
        verify(postRepository, times(1)).findById(any());
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toDto(any());
        assertEquals(String.format(CommentValidator.POST_NOT_FOUND, commentDto.getPostId()), entityNotFoundException.getMessage());
    }

    @Test
    public void updateCommentSuccessTest() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = getComment(now);
        CommentDto commentDto = getCommentDto(now);

        when(userContext.getUserId()).thenReturn(commentId);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenReturn(comment);
        when(commentMapper.toDto(any())).thenReturn(commentDto);

        CommentDto actualResult = commentService.updateComment(commentDto);

        verify(userContext, times(1)).getUserId();
        verify(commentRepository, times(1)).findById(any());
        verify(commentRepository, times(1)).save(any());
        verify(commentMapper, times(1)).toDto(any());
        assertThat(commentDto.getId()).isEqualTo(actualResult.getId());
        assertThat(commentDto.getAuthorId()).isEqualTo(actualResult.getAuthorId());
        assertThat(commentDto.getPostId()).isEqualTo(actualResult.getPostId());
        assertThat(commentDto.getContent()).isEqualTo(actualResult.getContent());
        assertThat(commentDto.getCreatedAt()).isEqualTo(actualResult.getCreatedAt());
        assertThat(commentDto.getUpdatedAt()).isEqualTo(actualResult.getUpdatedAt());
    }

    @Test
    public void updateCommentWithExceptionFailTest() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = getCommentDto(now);
        when(userContext.getUserId()).thenReturn(commentId);
        doNothing().when(postValidator).validatePostExist(anyLong());
        when(commentRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(commentDto)
                );

        verify(userContext, times(1)).getUserId();
        verify(commentRepository, times(1)).findById(any());
        verify(commentRepository, never()).save(any());
        assertEquals(String.format(CommentValidator.COMMENT_NOT_FOUND, commentDto.getId()), entityNotFoundException.getMessage());
    }

    @Test
    public void getAllCommentsSuccessTest() {
        LocalDateTime now = LocalDateTime.now();
        List<CommentDto> commentDtos = List.of(
                getCommentDto(now)
        );

        List<Comment> comments = List.of(
                getComment(now)
        );

        doNothing().when(postValidator).validatePostExist(postId);
        when(commentMapper.toDto(any())).thenReturn(commentDtos.get(0));
        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);

        List<CommentDto> actualResult = commentService.getAllComments(postId);

        assertThat(actualResult.size()).isEqualTo(1);
        verify(postValidator, times(1)).validatePostExist(postId);
        verify(commentRepository, times(1)).findAllByPostId(postId);
        assertThat(commentDtos.get(0).getId()).isEqualTo(actualResult.get(0).getId());
        assertThat(commentDtos.get(0).getAuthorId()).isEqualTo(actualResult.get(0).getAuthorId());
        assertThat(commentDtos.get(0).getPostId()).isEqualTo(actualResult.get(0).getPostId());
        assertThat(commentDtos.get(0).getContent()).isEqualTo(actualResult.get(0).getContent());
        assertThat(commentDtos.get(0).getCreatedAt()).isEqualTo(actualResult.get(0).getCreatedAt());
        assertThat(commentDtos.get(0).getUpdatedAt()).isEqualTo(actualResult.get(0).getUpdatedAt());
    }

    @Test
    public void getAllCommentsWithEmptyResultSuccessTest() {
        doNothing().when(postValidator).validatePostExist(postId);
        when(commentRepository.findAllByPostId(postId)).thenReturn(new ArrayList<>());

        List<CommentDto> actualResult = commentService.getAllComments(postId);

        assertThat(actualResult.size()).isEqualTo(0);
        verify(postValidator, times(1)).validatePostExist(postId);
        verify(commentRepository, times(1)).findAllByPostId(postId);
    }

    @Test
    public void deleteCommentSuccessTest() {
        doNothing().when(commentValidator).validateCommentExist(commentId);
        doNothing().when(commentRepository).deleteById(commentId);

        commentService.deleteComment(commentId);

        verify(commentValidator, times(1)).validateCommentExist(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    public void deleteCommentWithDeleteExceptionFailTest() {
        doNothing().when(commentValidator).validateCommentExist(commentId);
        doThrow(IllegalArgumentException.class).when(commentRepository).deleteById(commentId);

        assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(commentId));

        verify(commentValidator, times(1)).validateCommentExist(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    private CommentDto getCommentDto(LocalDateTime now) {
        return new CommentDto() {{
            setId(commentId);
            setAuthorId(authorId);
            setPostId(postId);
            setContent(content);
            setLikeIds(new ArrayList<>());
            setCreatedAt(now);
            setUpdatedAt(now);
        }};
    }

    private Comment getComment(LocalDateTime now) {
        return Comment.builder()
                .id(commentId)
                .authorId(authorId)
                .post(getPost())
                .content(content)
                .likes(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private Post getPost() {
        return Post.builder()
                .id(postId)
                .build();
    }
}
