package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {
    private Long id;
    private String content;
    private Long authorId;
    private List<Long> likeIds = new CopyOnWriteArrayList<>();
    private Long postId;
}
