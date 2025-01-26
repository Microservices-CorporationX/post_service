package faang.school.postservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "User", timeToLive = 84000)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUserWithAvatar {
    @Id
    private Long id;
    private String username;
    private String smallAvatarId;
}
