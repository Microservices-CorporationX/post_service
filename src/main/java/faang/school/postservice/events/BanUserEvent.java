package faang.school.postservice.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BanUserEvent {
    long userId;

    long commentCount;
}
