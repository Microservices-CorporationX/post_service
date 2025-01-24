package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@Builder
@RedisHash("comment")
@AllArgsConstructor
@NoArgsConstructor
public class CommentCache {
    
    private Long id;
    private String content;
    private Long authorId;
    private Long likesCount;
    private LocalDateTime createdAt;
    
}
