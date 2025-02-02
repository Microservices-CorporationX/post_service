package faang.school.postservice.dto.like;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LikeDto(
        @NotNull
        @Positive
        Long userId,
        Long commentId,
        Long postId) {
}
