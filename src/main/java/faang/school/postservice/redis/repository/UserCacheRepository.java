package faang.school.postservice.redis.repository;

import faang.school.postservice.config.redis.JedisConfig;
import faang.school.postservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UserCacheRepository {
    private final RedisTemplate<Long, UserDto> redisTemplate;
    private final JedisConfig redisConfig;

    public void cacheUser(UserDto userDto) {
        redisTemplate.opsForValue().set(userDto.getId(), userDto, redisConfig.getUsersTtl(), TimeUnit.SECONDS);
    }

    public UserDto getUserBy(Long userId) {
        return redisTemplate.opsForValue().get(userId);
    }
}
