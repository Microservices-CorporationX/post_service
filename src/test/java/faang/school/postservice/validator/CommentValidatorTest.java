package faang.school.postservice.validator;

import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentValidator commentValidator;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptorFirst;

    private final Long commentId = 1L;

    @Test
    public void validateCommentExistWithExistRecommendationSuccessTest() {

        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentValidator.validateCommentExist(commentId);

        verify(commentRepository, times(1)).
                existsById(longArgumentCaptorFirst.capture());
        assertEquals(commentId, longArgumentCaptorFirst.getValue());
    }

    @Test
    public void validateCommentExistWithNotExistCommentFailTest() {
        when(commentRepository.existsById(commentId)).thenReturn(false);

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                        () -> commentValidator.validateCommentExist(commentId)
                );

        assertEquals(String.format(CommentValidator.COMMENT_NOT_EXIST_BY_ID, commentId), illegalArgumentException.getMessage());
        verify(commentRepository, times(1)).
                existsById(longArgumentCaptorFirst.capture());
        assertEquals(commentId, longArgumentCaptorFirst.getValue());
    }
}
