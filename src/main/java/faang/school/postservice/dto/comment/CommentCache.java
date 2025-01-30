package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@RedisHash("Comment")
@AllArgsConstructor
@NoArgsConstructor
public class CommentCache implements Serializable {

    @Id
    private String id;
    private String content;
    private Long authorId;
    private Long likesCount;
    private LocalDateTime createdAt;

    @TimeToLive(unit = TimeUnit.DAYS)
    private Long ttl;
    
}
