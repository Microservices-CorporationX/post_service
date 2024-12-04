package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdatePostDto(
        @NotBlank(message = "Content cannot be blank")
        String content
) {
}
