package faang.school.postservice.validator;

import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {
    public final static String POST_NOT_EXIST_BY_ID = "Post with id = %s does not exist";

    private final PostRepository postRepository;

    public void validatePostExist(Long postId) {
        if (!postRepository.existsById(postId)) {
            String error = String.format(POST_NOT_EXIST_BY_ID, postId);
            throw new EntityNotFoundException(error);
        }
    }
}