package faang.school.postservice.validator;

import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    public final static String COMMENT_NOT_EXIST_BY_ID = "Comment with id = %s does not exist";
    public final static String COMMENT_NOT_FOUND = "Comment with id = %s not found";
    public final static String POST_NOT_FOUND = "Post with id = %s not found";

    private final CommentRepository commentRepository;

    public void validateCommentExist(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            String error = String.format(COMMENT_NOT_EXIST_BY_ID, commentId);
            throw new IllegalArgumentException(error);
        }
    }
}
