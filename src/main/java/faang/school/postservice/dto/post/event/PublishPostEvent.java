package faang.school.postservice.dto.post.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishPostEvent {
    private long postId;
    private List<Long> followers;
    private LocalDateTime publishedAt;
}
