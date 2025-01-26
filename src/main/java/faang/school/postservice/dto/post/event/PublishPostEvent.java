package faang.school.postservice.dto.post.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishPostEvent {
    private long postId;
    private List<Long> followers;
}
