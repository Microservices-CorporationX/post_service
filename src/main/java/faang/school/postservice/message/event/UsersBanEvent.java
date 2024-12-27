package faang.school.postservice.message.event;

import java.util.List;

public record UsersBanEvent(
        List<Long> userIdsToBan
) {
}
