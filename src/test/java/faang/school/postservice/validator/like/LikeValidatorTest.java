package faang.school.postservice.validator.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {
    @InjectMocks
    private LikeValidator likeValidator;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;

    @Test
    public void testValidationWhereIsLikePlacedIfCommentAndPostAreNotNull() {
        LikeDto likeDto = LikeDto.builder()
                .postId(1L)
                .commentId(2L)
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateWhereIsLikePlaced(likeDto));

        assertEquals("You can't like a post and a comment at the same time", exception.getMessage());
    }

    @Test
    public void testValidationWhereIsLikePlacedIfSameUserLikeSamePost() {
        LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .postId(1L)
                .build();
        when(likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId())).thenReturn(Optional.of(new Like()));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateWhereIsLikePlaced(likeDto));

        assertEquals("The same user cannot like the same post", exception.getMessage());
    }

    @Test
    public void testValidationWhereIsLikePlacedIfSameUserLikeSameComment() {
        LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .commentId(1L)
                .build();
        when(likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId())).thenReturn(Optional.of(new Like()));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateWhereIsLikePlaced(likeDto));

        assertEquals("The same user cannot like the same comment", exception.getMessage());
    }

    @Test
    public void testValidationWhereIsLikePlacedPassed() {
        LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .commentId(1L)
                .build();

        boolean resultAfterValidation = likeValidator.validateWhereIsLikePlaced(likeDto);

        assertDoesNotThrow(() -> likeValidator.validateWhereIsLikePlaced(likeDto));
        assertTrue(resultAfterValidation);
    }

    @Test
    public void testValidationIfCommentDoesNotExist() {
        LikeDto likeDto = LikeDto.builder().build();
        long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateComment(commentId, likeDto));

        assertEquals("Comment does not exist", exception.getMessage());
    }

    @Test
    public void testValidationIfLikeUnderCommentFromUserDoesNotExist() {
        LikeDto likeDto = LikeDto.builder()
                .id(3L)
                .commentId(1L)
                .userId(2L)
                .build();
        long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateComment(commentId, likeDto));

        assertEquals("Like does not exist", exception.getMessage());
    }

    @Test
    public void testValidationIfCommentExists() {
        LikeDto likeDto = LikeDto.builder()
                .id(3L)
                .commentId(1L)
                .userId(2L)
                .build();
        long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId()))
                .thenReturn(Optional.of(new Like()));

        boolean resultAfterValidation = likeValidator.validateComment(commentId, likeDto);

        assertDoesNotThrow(() -> likeValidator.validateComment(commentId, likeDto));
        assertTrue(resultAfterValidation);
    }

    @Test
    public void testValidationIfPostDoesNotExist() {
        LikeDto likeDto = LikeDto.builder()
                .postId(1L)
                .userId(2L)
                .build();
        long postId = 1L;
        when(postRepository.existsById(likeDto.getPostId())).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validatePost(postId, likeDto));

        assertEquals("Post does not exist", exception.getMessage());
    }

    @Test
    public void testValidationIfLikeUnderPostFromUserDoesNotExist() {
        LikeDto likeDto = LikeDto.builder()
                .id(3L)
                .postId(1L)
                .userId(2L)
                .build();
        long postId = 1L;
        when(postRepository.existsById(likeDto.getPostId())).thenReturn(true);
        when(likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validatePost(postId, likeDto));

        assertEquals("Like does not exist", exception.getMessage());
    }

    @Test
    public void testValidationIfPostExists() {
        LikeDto likeDto = LikeDto.builder()
                .id(3L)
                .postId(1L)
                .userId(2L)
                .build();
        long postId = 1L;
        when(postRepository.existsById(likeDto.getPostId())).thenReturn(true);
        when(likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId()))
                .thenReturn(Optional.of(new Like()));

        boolean resultAfterValidation = likeValidator.validatePost(postId, likeDto);

        assertDoesNotThrow(() -> likeValidator.validatePost(postId, likeDto));
        assertTrue(resultAfterValidation);
    }

    @Test
    public void testValidationIfUserDoesNotExist() {
        long userId = 1L;
        when(likeValidator.validateUser(userId)).thenThrow(FeignException.class);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateUser(userId));

        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    public void testValidationIfUserExists() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .build();
        when(userServiceClient.getUser(userDto.getId())).thenReturn(userDto);

        boolean resultAfterValidation = likeValidator.validateUser(userDto.getId());

        assertDoesNotThrow(() -> likeValidator.validateUser(userDto.getId()));
        assertTrue(resultAfterValidation);
    }
}
