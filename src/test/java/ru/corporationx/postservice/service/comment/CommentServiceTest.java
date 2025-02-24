package ru.corporationx.postservice.service.comment;

import ru.corporationx.postservice.dto.comment.CommentDto;
import ru.corporationx.postservice.exception.DataValidationException;
import ru.corporationx.postservice.mapper.comment.CommentMapper;
import ru.corporationx.postservice.model.Comment;
import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.publisher.comment.CommentEventPublisher;
import ru.corporationx.postservice.repository.CommentRepository;
import ru.corporationx.postservice.service.comment.CommentService;
import ru.corporationx.postservice.service.post.PostService;
import ru.corporationx.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    CommentValidator commentValidator;
    @Mock
    PostService postService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentMapper commentMapper;
    @Mock
    CommentEventPublisher commentEventPublisher;
    @InjectMocks
    CommentService commentService;

    @Test
    void testCreate() {
        CommentDto commentDto = CommentDto.builder()
                .authorId(1L)
                .content("1234")
                .postId(1L)
                .build();
        Comment comment = Comment.builder().id(3L).authorId(2L).build();
        Mockito.when(postService.findEntityById(anyLong())).thenReturn(Post.builder().id(2L).build());
        Mockito.when(commentMapper.toEntity(any())).thenReturn(comment);
        Mockito.when(commentRepository.save(comment)).thenReturn(comment);

        commentService.createComment(commentDto);
        Mockito.verify(commentRepository, times(1)).save(any());
        Mockito.verify(commentEventPublisher, times(1)).publish(any());
    }

    @Test
    void testCreateFailedValidation() {
        CommentDto commentDto = CommentDto.builder().content("1234").postId(1L).build();
        Mockito.doThrow(new DataValidationException("fail")).when(commentValidator).validateCreation(any());

        assertThrows(DataValidationException.class, () -> commentService.createComment(commentDto));
        Mockito.verify(commentRepository, times(0)).save(any());
    }

    @Test
    void testCreatePostNotFound() {
        CommentDto commentDto = CommentDto.builder().content("1234").postId(1L).build();
        Mockito.when(postService.findEntityById(anyLong())).thenThrow(new DataValidationException("fail"));

        assertThrows(DataValidationException.class, () -> commentService.createComment(commentDto));
        Mockito.verify(commentRepository, times(0)).save(any());
    }


    @Test
    void testUpdateComment() {
        CommentDto commentDto = CommentDto.builder().id(1L).build();
        Mockito.when(commentRepository.findById(anyLong())).thenReturn(Optional.of(new Comment()));
        commentService.updateComment(commentDto);
        Mockito.verify(commentRepository, times(1)).save(any());
    }

    @Test
    void testUpdateCommentNotValid() {
        CommentDto commentDto = CommentDto.builder().build();

        assertThrows(DataValidationException.class, () -> commentService.updateComment(commentDto));
        Mockito.verify(commentRepository, times(0)).save(any());
    }

    @Test
    void testUpdateCommentValidationFail() {
        CommentDto commentDto = CommentDto.builder().id(1L).build();
        Mockito.when(commentRepository.findById(anyLong())).thenReturn(Optional.of(new Comment()));
        Mockito.doThrow(new DataValidationException("fail")).when(commentValidator).validateUpdate(any(), any());

        assertThrows(DataValidationException.class, () -> commentService.updateComment(commentDto));
        Mockito.verify(commentRepository, times(0)).save(any());
    }

    @Test
    void testGetComments() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setCreatedAt(LocalDateTime.now().minusDays(2));
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setCreatedAt(LocalDateTime.now().minusDays(1));
        Comment comment3 = new Comment();
        comment3.setId(3L);
        comment3.setCreatedAt(LocalDateTime.now().minusDays(3));

        List<Comment> comments = List.of(comment1, comment2, comment3);
        Mockito.when(commentRepository.findAllByPostId(anyLong())).thenReturn(comments);

        Mockito.when(commentMapper.toDto(comment1)).thenReturn(CommentDto.builder().id(comment1.getId()).build());
        Mockito.when(commentMapper.toDto(comment2)).thenReturn(CommentDto.builder().id(comment2.getId()).build());
        Mockito.when(commentMapper.toDto(comment3)).thenReturn(CommentDto.builder().id(comment3.getId()).build());

        List<CommentDto> foundedComments = commentService.getComments(1);

        assertAll(
                () -> assertEquals(3, foundedComments.size()),
                () -> assertEquals(3, foundedComments.get(0).getId()),
                () -> assertEquals(1, foundedComments.get(1).getId()),
                () -> assertEquals(2, foundedComments.get(2).getId())
        );
    }

    @Test
    void testDelete() {
        Mockito.doNothing().when(commentRepository).deleteById(anyLong());
        assertDoesNotThrow(() -> commentService.deleteComment(1));
    }
}
