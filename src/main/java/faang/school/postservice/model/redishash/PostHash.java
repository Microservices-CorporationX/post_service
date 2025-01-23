package faang.school.postservice.model.redishash;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Post")
public class PostHash implements Serializable {
  @Id
  private Long id;
  private String content;
  private Long authorId;
  private Long projectId;
  private List<Long> albumsIds;
  private List<Long> resourcesIds;
  private boolean published;
  private boolean deleted;
  private LocalDateTime publishedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
