package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {

    @InjectMocks
    private LikeValidator likeValidator;

    @Mock
    private UserServiceClient userServiceClient;

    private LikeDto likeDto;
    private long userId;
    private boolean existsPost;
    private boolean existsComment;
    private boolean existsLikeByPostIdAndUserId;
    private boolean existsLikeByCommentIdAndUserId;

    @BeforeEach
    public void setUp() {
        likeDto = new LikeDto();
        userId = 1L;
        existsPost = true;
        existsComment = true;
        existsLikeByPostIdAndUserId = false;
        existsLikeByCommentIdAndUserId = false;
    }
    @Test
    public void testValidateAddedUserNotFound() {
        doThrow(FeignException.class).when(userServiceClient).getUserById(userId);

        validateAddedAssertThrow(new EntityNotFoundException(), String.format("User with id %d not found", userId));
    }

    @Test
    public void testAddedLikeOnPostAndComment() {
        likeDto.setPostId(1L);
        likeDto.setCommentId(1L);

        validateAddedAssertThrow(new IllegalStateException(), "Like must be added on post or comment");
    }

    @Test
    public void testValidateAddedPostAndCommentIsNull() {
        validateAddedAssertThrow(new IllegalStateException(), "Like must be added on post or comment");
    }

    @Test
    public void testValidateAddedPostNotExists() {
        likeDto.setPostId(1L);
        existsPost = false;

        validateAddedAssertThrow(new IllegalStateException(), "Post does not exist");
    }

    @Test
    public void testValidateAddedExistsLikeByPostIdAndUserId() {
        likeDto.setPostId(1L);
        existsLikeByPostIdAndUserId = true;

        validateAddedAssertThrow(new IllegalStateException(), "Like on this post already exists");
    }

    @Test
    public void testValidateAddedCommentNotExists() {
        likeDto.setCommentId(1L);
        existsComment = false;

        validateAddedAssertThrow(new IllegalStateException(), "Comment does not exist");
    }

    @Test
    public void testValidateAddedExistsLikeByCommentIdAndUserId() {
        likeDto.setCommentId(1L);
        existsLikeByCommentIdAndUserId = true;

        validateAddedAssertThrow(new IllegalStateException(), "Like on this comment already exists");
    }

    @Test
    public void testValidateAddedSuccessful() {
        likeDto.setPostId(1L);

        likeValidator.validateAdded(
                likeDto,
                userId,
                existsPost,
                existsComment,
                existsLikeByPostIdAndUserId,
                existsLikeByCommentIdAndUserId
        );
    }

    @Test
    public void testValidateDeletedLikeNotExists() {
        boolean existsLikeById = false;

        assertThrows(IllegalStateException.class,
                () -> likeValidator.validateDeleted(existsLikeById));
    }

    @Test
    public void testValidateDeletedSuccessful() {
        boolean existsLikeById = true;

    likeValidator.validateDeleted(existsLikeById);
    }

    private void validateAddedAssertThrow(RuntimeException e, String message) {
        RuntimeException exception = assertThrows(e.getClass(), () -> likeValidator.validateAdded(
                likeDto,
                userId,
                existsPost,
                existsComment,
                existsLikeByPostIdAndUserId,
                existsLikeByCommentIdAndUserId
        ));

        assertEquals(message, exception.getMessage());
    }
}
