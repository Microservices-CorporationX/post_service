package faang.school.postservice.dto.kafka.event;

import lombok.Builder;

@Builder
public record LikeEventDto (
    long postId,
    long likeId
) {}
