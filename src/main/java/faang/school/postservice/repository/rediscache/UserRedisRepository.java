package faang.school.postservice.repository.rediscache;

import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UserRedisRepository {

    private final RedisTemplate<String, Object> userRedisTemplate;
    private static final long TTL_SECONDS = 86400;

    public void save(String key, Object value) {
        userRedisTemplate.opsForValue().set(key, value, Duration.ofSeconds(TTL_SECONDS));
    }

    public UserDto findUserByKey(String key) {
        return (UserDto) userRedisTemplate.opsForValue().get(key);
    }
}
