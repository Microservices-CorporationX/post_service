package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record LikeCommentDto(@NotNull @Positive Long userId,
                             @NotNull @Positive Long commentId) {
}
