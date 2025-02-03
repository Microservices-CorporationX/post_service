package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CommentFiltersDto(
        @NotNull
        Long postId
) {
}
