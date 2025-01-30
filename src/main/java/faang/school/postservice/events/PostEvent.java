package faang.school.postservice.events;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class PostEvent {
    private long postId;
    private long authorId;
    private LocalDateTime publishedAt;
    private List<Long> followerIds;
}
