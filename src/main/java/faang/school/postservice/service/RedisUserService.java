package faang.school.postservice.service;

import faang.school.postservice.model.dto.redis.cache.RedisUserDto;
import faang.school.postservice.model.entity.UserShortInfo;

import java.util.List;
import java.util.Map;

public interface RedisUserService {
    void saveUserIfNotExists(RedisUserDto userDto);

    RedisUserDto getUser(Long userId);

    void saveUser(RedisUserDto redisUserDto);

    void saveUsers(List<RedisUserDto> userDtos);

    List<Long> getFollowerIds(Long userId);

    void updateUserIfStale(UserShortInfo userShortInfo, int refreshTime);
}
