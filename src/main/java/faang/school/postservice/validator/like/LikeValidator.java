package faang.school.postservice.validator.like;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.exception.DataValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    public void validateLikePost(@NotNull LikePostDto dto) {
        if (dto.postId() == null) {
            throw new DataValidationException("Id поста не может быть равно null");
        }
    }

    public void validateLikeComment(@NotNull LikeCommentDto dto) {
        if (dto.commentId() == null) {
            throw new DataValidationException("Id коммента не может быть равно null");
        }
    }
}
