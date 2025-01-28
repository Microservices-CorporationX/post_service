package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCache {
    private Long id;
    private long authorId;
    private String content;
    private LocalDateTime createdAt;
}
