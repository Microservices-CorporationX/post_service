package faang.school.postservice.dto.like;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public record LikeEvent(
        Long postId,
        Long authorId,
        Long userId,
        LocalDateTime timestamp
) {
}
