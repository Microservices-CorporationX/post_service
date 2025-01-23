package faang.school.postservice.event;

import lombok.Builder;

import java.util.List;

@Builder
public record PostPublishedEvent(
        Long postId,
        Long authorId,
        List<Long> followersId
) {
}
