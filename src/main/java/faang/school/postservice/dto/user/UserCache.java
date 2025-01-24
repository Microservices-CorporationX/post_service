package faang.school.postservice.dto.user;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("User")
public class UserCache implements Serializable {

    @Id
    private Long id;
    private String username;

    @TimeToLive(unit = TimeUnit.DAYS)
    private long ttl;
}
