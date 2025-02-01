package faang.school.postservice.model.cache;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@RedisHash(value = "feed")
public class FeedCache {

    @Id
    private Long userId;
    private List<Long> postsId;
}
