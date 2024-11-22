package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdatePostDto(
        @NotNull(message = "Content cannot be null")
        String content
) {
}
