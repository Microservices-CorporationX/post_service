package faang.school.postservice.model.cache;


import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;

@RedisHash(value = "post")
@RequiredArgsConstructor
@Getter
@Setter
public class PostCache {

    @Id
    private Long id;
    private String content;
    private Long authorId;
    private List<Long> followersId;

    @TimeToLive
    @Value("${spring.data.redis.cache.ttl}")
    private long ttl;
}
