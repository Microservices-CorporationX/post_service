package faang.school.postservice.service.redis;

import faang.school.postservice.dto.post.PostRedis;
import faang.school.postservice.dto.user.UserNFDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void savePost(PostRedis post) {
        redisTemplate.opsForHash().put("posts", String.valueOf(post.getId()), post);
    }

    @Override
    public void saveUser(UserNFDto user) {
        redisTemplate.opsForHash().put("users", String.valueOf(user.getId()), user);
    }
}
