package faang.school.postservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeEvent {

    private Long commentId;
    private Long userId;
    private boolean isLikeAdded;
}
