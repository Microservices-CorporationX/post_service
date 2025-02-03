package faang.school.postservice.model.cache;


import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;
import java.util.TreeSet;

@RedisHash(value = "post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostEvent {
    @Id
    private Long id;
    private TreeSet<CommentEvent> comments;
    private Long authorId;
    private List<Long> followersId;
    private int likesCount;
    private int viewsCount;

    @Version
    private Long version;

    @TimeToLive
    @Value("${spring.data.redis.cache.ttl}")
    private long ttl;
}
