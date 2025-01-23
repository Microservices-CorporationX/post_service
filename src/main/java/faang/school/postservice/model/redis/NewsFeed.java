package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("newsFeed")
public class NewsFeed {


}
