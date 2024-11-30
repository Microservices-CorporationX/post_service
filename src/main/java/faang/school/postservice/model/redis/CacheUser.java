package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "users", timeToLive = 86400L)
public class CacheUser {
    @Id
    private Long id;
    private String username;
    private String email;
    private List<Long> followeesIds;
}
