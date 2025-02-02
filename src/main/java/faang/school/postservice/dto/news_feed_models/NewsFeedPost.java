package faang.school.postservice.dto.news_feed_models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash(value = "Post")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NewsFeedPost implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long postId;
    private String content;
    private Long authorId;
    private String authorName;
    private Long countViews;
    private LocalDateTime publishedAt;
}