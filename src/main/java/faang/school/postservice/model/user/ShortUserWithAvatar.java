package faang.school.postservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "User", timeToLive = 86400)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUserWithAvatar {
    private Long id;
    private String username;
    private String smallAvatarId;
}
