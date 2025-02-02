package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentKafkaEvent {
    private Long id;
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime updateAt;
}
