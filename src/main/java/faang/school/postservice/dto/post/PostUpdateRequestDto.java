package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PostUpdateRequestDto(@NotBlank String content) {
}
