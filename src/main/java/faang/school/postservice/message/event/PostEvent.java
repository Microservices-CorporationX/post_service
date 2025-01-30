package faang.school.postservice.message.event;

import java.util.List;

public record PostEvent (
        Long postId,
        List<Long> followerIds
) {
}
