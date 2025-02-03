package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentUpdateDto(
        @NotNull
        @Size(min = 1, max = 4096, message = "Content must not exceed 4096 characters")
        String content
) {
}
