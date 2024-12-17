package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentServiceValidator {

    private final UserServiceClient userServiceClient;

    private final PostService postService;

    private final CommentRepository commentRepository;

    public void validateCreateComment(CommentDto commentDto) {
        try {
            userServiceClient.getUserById(commentDto.getAuthorId());
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with id %s not found".formatted(commentDto.getAuthorId()));
        }
    }

    public void validatePostId(Long postId) {
        if (postService.findPostById(postId).isEmpty()) {
            throw new EntityNotFoundException("Post with id %s not found".formatted(postId));
        }
    }

    public void validateCommentId(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment with id %s not found".formatted(commentId));
        }
    }
}
