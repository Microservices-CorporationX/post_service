package faang.school.postservice.dto.news_feed_models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

@RedisHash(value = "Author")
@Data
@AllArgsConstructor
public class NewsFeedAuthor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private long userId;
    private String username;
}