package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentEvent;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producers.KafkaCommentProducer;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentEventPublisher redisCommentPublisher;
    @Mock
    private KafkaCommentProducer kafkaCommentProducer;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Comment comment;
    private Post post;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto();
        commentDto.setPostId(1L);
        commentDto.setAuthorId(2L);
        commentDto.setContent("Test comment");

        post = new Post();
        post.setId(1L);
        post.setAuthorId(3L);

        userDto = new UserDto();
        userDto.setId(2L);

        comment = new Comment();
        comment.setId(1L);
        comment.setAuthorId(2L);
        comment.setContent("Test comment");
        comment.setPost(post);
    }

    @Test
    void createComment_Success() {
        when(postRepository.findById(commentDto.getPostId())).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(userDto);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getContent(), result.getContent());
        verify(redisCommentPublisher, times(1)).publishMessage(any(CommentEvent.class));
        verify(kafkaCommentProducer, times(1)).publishEvent(any(CommentEvent.class));
    }

    @Test
    void createComment_PostNotFound() {
        when(postRepository.findById(commentDto.getPostId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> commentService.createComment(commentDto));
        assertEquals("Post not found with ID: " + commentDto.getPostId(), exception.getMessage());
    }

    @Test
    void createComment_UserNotFound() {
        when(postRepository.findById(commentDto.getPostId())).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(null);

        Exception exception = assertThrows(IllegalStateException.class, () -> commentService.createComment(commentDto));
        assertEquals("User not found with ID: " + commentDto.getAuthorId(), exception.getMessage());
    }

    @Test
    void updateComment_Success() {
        long commentId = 1L;
        commentDto.setContent("Updated content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(commentId, commentDto);

        assertNotNull(result);
        assertEquals("Updated content", result.getContent());
    }

    @Test
    void updateComment_NotAuthor() {
        long commentId = 1L;
        commentDto.setAuthorId(99L);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(commentId, commentDto));

        assertEquals("Only the author of the comment can update it.", exception.getMessage());
    }

    @Test
    void updateComment_EmptyContent() {
        long commentId = 1L;
        commentDto.setContent(" ");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(commentId, commentDto));

        assertEquals("Content cannot be blank.", exception.getMessage());
    }

    @Test
    void getCommentsByPostId() {
        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        List<CommentDto> comments = commentService.getCommentsByPostId(1L);

        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
        assertEquals(commentDto.getContent(), comments.get(0).getContent());
    }

    @Test
    void deleteComment_Success() {
        long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(true);
        doNothing().when(commentRepository).deleteById(commentId);

        assertDoesNotThrow(() -> commentService.deleteComment(commentId));

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void deleteComment_NotFound() {
        long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(commentId));

        assertEquals("Comment not found with ID: " + commentId, exception.getMessage());
    }
}
