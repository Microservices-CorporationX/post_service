package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "Posts")
public class PostRedis {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<LikeRedis> likes;
    private TreeSet<CommentRedis> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private int views;
}
