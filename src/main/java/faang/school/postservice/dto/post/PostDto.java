package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record PostDto(
        @NotBlank(message = "Content should not be blank")
        @Size(max = 255, message = "Content must not exceed 255 characters")
        String content,
        @Min(1)
        Long userId,
        @Min(1)
        Long projectId,
        LocalDateTime scheduledAt) {
}
