package faang.school.postservice.validator;

import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostValidator postValidator;

    @Test
    public void validatePostExistSuccessTest() {
        Long postId = 1L;
        when(postRepository.existsById(postId)).thenReturn(true);

        postValidator.validatePostExist(postId);

        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    public void validatePostExistFailTest() {
        Long postId = 1L;
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> postValidator.validatePostExist(postId)
        );

        verify(postRepository, times(1)).existsById(postId);
    }
}
