package faang.school.postservice.dto.feedheat;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class FeedHeatEvent {
    private List<Long> userIds;
}
