package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("newsFeed")
public class NewsFeed {

    @Id
    public Long userId;
    public Set<Long> postIds;

}
