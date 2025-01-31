package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.LinkedHashSet;
import lombok.Builder;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

@Builder
@RedisHash("Feed")
public class FeedCache implements Serializable {

  @Id
  private Long userId;

  private LinkedHashSet<Long> postsIds;

  @Version
  private int version;

  public void addPost(Long postId) {
    postsIds.add(postId);
    version++;
  }

}
