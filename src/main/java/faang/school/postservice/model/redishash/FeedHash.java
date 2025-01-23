package faang.school.postservice.model.redishash;

import jakarta.persistence.Id;
import java.util.LinkedHashSet;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Feed")
public class FeedHash {
  @Id
  private Long userId;
  private LinkedHashSet<Long> postsIds;

}
