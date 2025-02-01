package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentMapperImpl commentMapper;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentService commentService;

    private static final long USER_ID = 1L;
    private static final long COMMENT_ID = 2L;
    private static final long POST_ID = 3L;
    private final UserDto userDto = UserDto.builder().id(USER_ID).build();
    private final Post post = Post.builder().id(POST_ID).authorId(USER_ID).build();
    private final CommentCreateDto commentCreateDto = CommentCreateDto.builder()
            .authorId(USER_ID)
            .content("content")
            .build();

    @Test
    void testAddCommentIfUserExist() {
        when(userService.getUser(anyLong())).thenReturn(userDto);
        when(postService.findById(anyLong())).thenReturn(post);

        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(post);
        CommentReadDto expected = CommentReadDto.builder()
                .authorId(USER_ID)
                .content(commentCreateDto.getContent())
                .postId(POST_ID)
                .build();

        when(commentRepository.save(comment)).thenReturn(comment);

        CommentReadDto result = commentService.addComment(anyLong(), commentCreateDto);
        assertEquals(expected, result);
    }

    @Test
    void testAddCommentThrowExceptionIfUserNotExists() {
        when(userService.getUser(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(BusinessException.class, () -> commentService.addComment(anyLong(), commentCreateDto));
    }

    @Test
    void testEditComment() {
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .content("updated content")
                .build();

        Comment comment = commentMapper.toEntity(commentCreateDto);
        Comment updatedComment = commentMapper.update(comment, commentUpdateDto);
        CommentReadDto expectedCommentDto = commentMapper.toReadDto(updatedComment);

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(updatedComment)).thenReturn(updatedComment);

        CommentReadDto result = commentService.editComment(anyLong(), commentUpdateDto);
        assertEquals(expectedCommentDto, result);
    }

    @Test
    void testEditCommentThrowExceptionIfCommentNotExists() {
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .content("updated content")
                .build();

        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.editComment(anyLong(), commentUpdateDto));
    }

    @Test
    void testGetComments() {
        Comment comment = commentMapper.toEntity(commentCreateDto);
        List<Comment> comments = List.of(comment);
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(comments);

        List<CommentReadDto> result = commentService.getComments(POST_ID);
        assertEquals(comments.size(), result.size());
    }

    @Test
    void testDeleteCommentThrowExceptionIfCommentNotExists() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(POST_ID, COMMENT_ID));
        verify(commentRepository, times(0)).deleteById(COMMENT_ID);
    }

    @Test
    void testDeleteCommentIfCommentBelongsToPost() {
        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(post);

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        commentService.deleteComment(POST_ID, COMMENT_ID);

        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    void testDeleteCommentIfCommentNotBelongsToPost() {
        Long notMatchPostId = 100L;
        Comment comment = commentMapper.toEntity(commentCreateDto);
        post.setId(notMatchPostId);
        comment.setPost(post);

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        assertThrows(BusinessException.class, () -> commentService.deleteComment(POST_ID, COMMENT_ID));
        verify(commentRepository, times(0)).deleteById(COMMENT_ID);
    }
}