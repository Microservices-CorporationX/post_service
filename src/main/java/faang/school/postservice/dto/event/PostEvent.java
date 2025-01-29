package faang.school.postservice.dto.event;

import java.util.List;
import lombok.Builder;

@Builder
public record PostEvent(
    Long postId,
    Long authorId,
    List<Long> subscribersIds
) {
}
