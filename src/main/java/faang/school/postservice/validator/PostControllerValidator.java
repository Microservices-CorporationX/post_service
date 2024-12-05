package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class PostControllerValidator {

    public void validatePostCreators(Long authorId, Long projectId) {
        if (authorId != null && projectId != null) {
            throw new DataValidationException("У поста не может быть двух авторов");
        }

        if (authorId == null && projectId == null) {
            throw new DataValidationException("Нет автора поста");
        }
    }
}
