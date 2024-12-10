package faang.school.postservice.publisher;

import faang.school.postservice.config.redis.RedisConfigProperties;
import faang.school.postservice.dto.AdBoughtEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdBoughtEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConfigProperties redisConfigProperties;

    public void publish(AdBoughtEvent adBoughtEvent) {
        redisTemplate.convertAndSend(redisConfigProperties.channel().ad_bought_channel(), adBoughtEvent);
    }
}
