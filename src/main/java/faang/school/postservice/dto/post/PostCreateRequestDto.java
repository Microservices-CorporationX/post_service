package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PostCreateRequestDto(
        @NotBlank
        String content,
        @NotNull
        Long authorId,
        @NotNull
        Long projectId) {
}
