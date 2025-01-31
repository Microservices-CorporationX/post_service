package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeEvent {
    private long likeId;
    private long userId;
    private long postId;
    private long commentId;
}
