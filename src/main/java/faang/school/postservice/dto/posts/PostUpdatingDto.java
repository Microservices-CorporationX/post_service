package faang.school.postservice.dto.posts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PostUpdatingDto(
        @Positive Long postId,
        @NotBlank String updatingContent
) {}
