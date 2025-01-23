package faang.school.postservice.dto.posts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;

public record PostUpdatingDto(
        @NonNull @Positive Long postId,
        @NotBlank String updatingContent
) {}
