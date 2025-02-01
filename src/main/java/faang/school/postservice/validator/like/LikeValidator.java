package faang.school.postservice.validator.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.db_repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LikeValidator {

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    public boolean validateCommentHasLike(long commentId, long userId) {
        Optional<Like> like = likeRepository.findByCommentIdAndUserId(commentId, userId);
        return like.isEmpty();

    }

    public boolean validatePostHasLike(long postId, long userId) {
        Optional<Like> like = likeRepository.findByPostIdAndUserId(postId, userId);
        return like.isEmpty();
    }

    public void validateUserId(Long userId) {
        userServiceClient.getUser(userId);
    }


}
