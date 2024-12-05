package faang.school.postservice.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostControllerValidatorTest {

    private PostControllerValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new PostControllerValidator();
    }

    @Test
    public void testAllCreatorsAbsents() {
        PostCreateDto postCreateDto = new PostCreateDto("content", null, null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validatePostCreators(postCreateDto.authorId(), postCreateDto.projectId()));
        assertThat(exception.getMessage()).isEqualTo("Нет автора поста");
    }

    @Test
    public void testBothCreatorsExists() {
        PostCreateDto postCreateDto = new PostCreateDto("content", 100L, 200L);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validatePostCreators(postCreateDto.authorId(), postCreateDto.projectId()));
        assertThat(exception.getMessage()).isEqualTo("У поста не может быть двух авторов");
    }
}