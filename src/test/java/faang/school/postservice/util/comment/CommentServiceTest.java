package faang.school.postservice.util.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    private Comment comment;
    private Post post;

    @BeforeEach
    public void setUp() {
        comment = Comment.builder()
                .id(1L)
                .authorId(10L)
                .content("Test comment")
                .updatedAt(LocalDateTime.now())
                .build();

        post = Post.builder()
                .id(1L)
                .content("Test post")
                .comments(List.of(comment))
                .build();
    }

    @Test
    public void testCreateComment() {
        // Arrange
        when(userServiceClient.getUser(comment.getAuthorId())).thenReturn(mock(UserDto.class));
        when(postService.getPostById(1L)).thenReturn(post);
        when(commentRepository.save(comment)).thenReturn(comment);

        // Act
        Comment result = commentService.createComment(comment, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(post, result.getPost());
        verify(userServiceClient).getUser(comment.getAuthorId());
        verify(postService).getPostById(1L);
        verify(commentRepository).save(comment);
    }

    @Test
    public void testCreateCommentUserNotFound() {
        when(userServiceClient.getUser(comment.getAuthorId())).thenThrow(new NoSuchElementException("User not found"));

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            commentService.createComment(comment, 1L);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(userServiceClient).getUser(comment.getAuthorId());
        verify(postService, never()).getPostById(anyLong());
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void testUpdateCommentCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            commentService.updateComment(comment);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(commentRepository).findById(1L);
    }

    @Test
    public void testUpdateComment() {
        Comment updatedComment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .content("Updated content")
                .updatedAt(LocalDateTime.now())
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comment result = commentService.updateComment(updatedComment);

        assertNotNull(result);
        assertEquals(updatedComment.getContent(), result.getContent());
        verify(commentRepository).findById(1L);
    }

    @Test
    public void testGetAllCommentsToPost() {
        when(postService.getPostById(1L)).thenReturn(post);

        List<Comment> comments = commentService.getAllCommentsToPost(1L);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        verify(postService).getPostById(1L);
    }

    @Test
    public void testGetAllCommentsToPost_PostNotFound() {
        when(postService.getPostById(1L)).thenThrow(new NoSuchElementException("Post not found"));

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            commentService.getAllCommentsToPost(1L);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postService).getPostById(1L);
    }

    @Test
    public void testDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository).findById(1L);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    public void testDeleteComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        commentService.deleteComment(1L);

        verify(commentRepository).findById(1L);
        verify(commentRepository, never()).deleteById(1L);
    }
}

