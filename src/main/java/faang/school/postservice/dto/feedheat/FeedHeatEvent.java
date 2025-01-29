package faang.school.postservice.dto.feedheat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class FeedHeatEvent {
    private List<Long> userIds;
}
