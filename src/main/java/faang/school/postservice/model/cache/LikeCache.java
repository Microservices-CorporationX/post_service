package faang.school.postservice.model.cache;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RequiredArgsConstructor
@RedisHash(value = "likes")
public class LikeCache {
    @Id
    private Long postId;
    private Long authorId;
}
