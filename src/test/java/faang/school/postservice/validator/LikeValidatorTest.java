package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {
    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private LikeValidator likeValidator;

    private final Long commentId = 3L;
    private final Long userId = 2L;
    private final Long postId = 1L;

    @Test
    void validatePostIdShouldThrowExceptionWhenPostIdIsNull() {
        assertThrows(DataValidationException.class, () -> likeValidator.validatePostId(null));
        verifyNoInteractions(postService);
    }

    @Test
    void validatePostIdShouldThrowExceptionWhenPostNotFound() {
        when(postService.getPostById(postId)).thenReturn(null);

        assertThrows(DataValidationException.class, () -> likeValidator.validatePostId(postId));
        verify(postService).getPostById(postId);
    }

    @Test
    void validatePostId_ShouldPass_WhenPostExists() {
        when(postService.getPostById(postId)).thenReturn(new Post());

        likeValidator.validatePostId(postId);

        verify(postService).getPostById(postId);
    }

    @Test
    void validateUserId_ShouldThrowException_WhenUserIdIsNull() {
        assertThrows(DataValidationException.class, () -> likeValidator.validateUserId(null));
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void validateUserIdShouldThrowExceptionWhenUserNotFound() {
        when(userServiceClient.getUser(userId)).thenReturn(null);

        assertThrows(DataValidationException.class, () -> likeValidator.validateUserId(userId));
        verify(userServiceClient).getUser(userId);
    }

    @Test
    void validateUserId_ShouldPass_WhenUserExists() {
        when(userServiceClient.getUser(userId)).thenReturn((new UserDto()));

        likeValidator.validateUserId(userId);

        verify(userServiceClient).getUser(userId);
    }

    @Test
    void validateCommentIdShouldThrowExceptionWhenCommentIdIsNull() {
        assertThrows(DataValidationException.class, () -> likeValidator.validateCommentId(null));
        verifyNoInteractions(commentService);
    }

    @Test
    void validateCommentIdShouldThrowExceptionWhenCommentNotFound() {
        when(commentService.getCommentById(commentId)).thenReturn(null);

        assertThrows(DataValidationException.class, () -> likeValidator.validateCommentId(commentId));
        verify(commentService).getCommentById(commentId);
    }

    @Test
    void validateCommentIdShouldPassWhenCommentExists() {
        when(commentService.getCommentById(commentId)).thenReturn(new Comment());

        likeValidator.validateCommentId(commentId);

        verify(commentService).getCommentById(commentId);
    }
}