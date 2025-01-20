package faang.school.postservice.dto.posts;

import jakarta.validation.constraints.Positive;
import lombok.*;

@Builder
public record PostCreatingRequest(@Positive long id, @NonNull String content, @Positive Long authorId, @Positive Long projectId) {
}