package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
}
