package faang.school.postservice.util.service;

import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    private static final long AUTHOR_ID = 1L;
    private static final long NOT_AUTHOR_ID = 2L;
    private static final long COMMENT_ID = 2L;
    private static final long ELSE_COMMENT_ID = 3L;
    private static final long POST_ID = 3L;

    @Spy
    private CommentMapperImpl commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void testCreateSuccessfully() {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setAuthorId(AUTHOR_ID);
        Mockito.doNothing().when(userService).verifyUserExists(createDto.getAuthorId());

        Comment comment = commentMapper.toEntity(createDto);

        commentService.create(createDto);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
    }

    @Test
    public void testUpdateSuccessfully() {
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setEditorId(AUTHOR_ID);
        updateDto.setId(COMMENT_ID);

        Comment comment = Comment.builder().id(COMMENT_ID).authorId(AUTHOR_ID).build();
        Mockito.when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.ofNullable(comment));

        commentMapper.updateEntityFromDto(updateDto, comment);

        commentService.update(updateDto);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);

    }

    @Test
    public void testUpdateFailsIfEditorIsNotTheAuthor() {
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setEditorId(NOT_AUTHOR_ID);
        updateDto.setId(COMMENT_ID);

        Comment comment = Comment.builder().id(COMMENT_ID).authorId(AUTHOR_ID).build();
        Mockito.when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.ofNullable(comment));

        assertThrows(BusinessException.class, () -> commentService.update(updateDto));
    }

    @Test
    public void testGetCommentsByPostId() {
        Comment comment1 = Comment.builder().id(COMMENT_ID).build();
        Comment comment2 = Comment.builder().id(ELSE_COMMENT_ID).build();
        List<Comment> comments = List.of(comment1, comment2);

        Mockito.when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<CommentReadDto> commentDtos = commentService.getCommentsByPostId(POST_ID);

        assertEquals(2, commentDtos.size());
    }

    @Test
    public void testRemoveSuccessfully() {
        commentService.remove(COMMENT_ID);
        Mockito.verify(commentRepository, Mockito.times(1)).deleteById(COMMENT_ID);
    }
}
