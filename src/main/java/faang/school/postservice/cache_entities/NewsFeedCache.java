package faang.school.postservice.cache_entities;

import lombok.Data;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;

@RedisHash("NewsFeed")
@Data
public class NewsFeedCache implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long newsFeedOwnerId;
    private LinkedHashSet<PostCache> posts;
}