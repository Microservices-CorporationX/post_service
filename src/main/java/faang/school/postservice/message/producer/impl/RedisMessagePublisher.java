package faang.school.postservice.message.producer.impl;

import faang.school.postservice.config.redis.RedisConfig;
import faang.school.postservice.message.producer.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service(RedisConfig.REDIS_PUBLISHER_NAME)
@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
