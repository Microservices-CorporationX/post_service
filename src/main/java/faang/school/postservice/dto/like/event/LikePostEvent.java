package faang.school.postservice.dto.like.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikePostEvent {
    private Long userId;
    private Long postId;
}
