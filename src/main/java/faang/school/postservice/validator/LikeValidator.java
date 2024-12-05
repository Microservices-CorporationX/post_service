package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentService commentService;

    public void validatePostId(Long postId) {
        if (postId == null) {
            log.error("Post id cannot be null");
            throw new DataValidationException("Post id cannot be null");
        }

        if (postService.getPostById(postId) == null) {
            log.error("Post with id {} not found", postId);
            throw new DataValidationException("Post with id " + postId + " not found");
        }
    }

    public void validateUserId(Long userId) {
        if (userId == null) {
            log.error("User id cannot be null");
            throw new DataValidationException("User id cannot be null");
        }

        if (userServiceClient.getUser(userId) == null) {
            log.error("User with id {} not found", userId);
            throw new DataValidationException("User with id " + userId + " not found");
        }
    }

    public void validateCommentId(Long commentId) {
        if (commentId == null) {
            log.error("Comment id cannot be null");
            throw new DataValidationException("Comment id cannot be null");
        }

        if (commentService.getCommentById(commentId) == null) {
            log.error("Comment with id {} not found", commentId);
            throw new DataValidationException("Comment with id " + commentId + " not found");
        }
    }
}
