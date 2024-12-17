package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeValidator {
    private final UserServiceClient userServiceClient;

    public void validateAdded(LikeDto dto, long userId, boolean existsPost, boolean existsComment, boolean existsLikeByPostIdAndUserId, boolean existsLikeByCommentIdAndUserId) {
        try {
            userServiceClient.getUserById(userId);
        } catch (FeignException e) {
            log.error(e.getMessage(), e);
            throw new EntityNotFoundException(String.format("User with id %d not found", userId));
        }

        if ((dto.getCommentId() == null) == (dto.getPostId() == null)) {
            throw new IllegalStateException("Like must be added on post or comment");
        }

        if (dto.getPostId() != null) {
            if (!existsPost) {
                throw new IllegalStateException("Post does not exist");
            }

            if (existsLikeByPostIdAndUserId) {
                throw new IllegalStateException("Like on this post already exists");
            }
        }

        if (dto.getCommentId() != null) {
            if (!existsComment) {
                throw new IllegalStateException("Comment does not exist");
            }

            if (existsLikeByCommentIdAndUserId) {
                throw new IllegalStateException("Like on this comment already exists");
            }
        }
    }

    public void validateDeleted(boolean existsLikeById) {
        if (!existsLikeById) {
            throw new IllegalStateException("Like dont exists");
        }
    }

}