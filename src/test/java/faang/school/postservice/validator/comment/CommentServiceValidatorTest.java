package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentServiceValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostService postService;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceValidator validator;

    @Test
    void validateCreateCommentSuccess() {
        Mockito.lenient().when(userServiceClient.getUserById(Mockito.anyLong())).thenReturn(null);
        assertDoesNotThrow(() -> validator.validateCreateComment(getCommentDto()));
    }

    @Test
    void validateCreateCommentFailure() {
        Mockito.lenient().when(userServiceClient.getUserById(Mockito.anyLong())).thenThrow(FeignException.class);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> validator.validateCreateComment(getCommentDto()));
        assertEquals("User with id %s not found".formatted(getCommentDto().getAuthorId()), exception.getMessage());
    }

    @Test
    void validatePostIdSuccess() {
        Mockito.lenient().when(postService.findPostById(Mockito.anyLong())).thenReturn(Optional.of(new Post()));
        assertDoesNotThrow(() -> validator.validatePostId(Mockito.anyLong()));
    }

    @Test
    void validatePostIdFailure() {
        Mockito.lenient().when(postService.findPostById(Mockito.anyLong())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> validator.validatePostId(getCommentDto().getPostId()));
        assertEquals("Post with id %s not found".formatted(getCommentDto().getPostId()), exception.getMessage());
    }

    @Test
    void validateCommentIdSuccess() {
        Mockito.lenient().when(commentRepository.existsById(Mockito.anyLong())).thenReturn(true);
        assertDoesNotThrow(() -> validator.validateCommentId(Mockito.anyLong()));
    }

    @Test
    void validateCommentIdFailure() {
        Mockito.lenient().when(commentRepository.existsById(Mockito.anyLong())).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> validator.validateCommentId(getCommentDto().getId()));
        assertEquals("Comment with id %s not found".formatted(getCommentDto().getId()), exception.getMessage());
    }

    private CommentDto getCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .content("Content 1")
                .authorId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}