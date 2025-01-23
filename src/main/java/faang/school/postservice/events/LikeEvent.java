package faang.school.postservice.events;

import lombok.Data;

@Data
public class LikeEvent {
    private long userId;
    private long postId;
    private long commentId;
}
