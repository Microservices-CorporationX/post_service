package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@RedisHash(value = "users", timeToLive = 86400)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCache implements Serializable {
    @Id
    private Long id;
    private String username;
    private List<Long> followerIds;
    private List<Long> followeeIds;
}
