package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LikeDto(
        @NotNull
        @Positive
        Long userId,
        @Positive
        Long commentId,
        @Positive
        Long postId) {
}
