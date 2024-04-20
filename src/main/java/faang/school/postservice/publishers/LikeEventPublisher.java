package faang.school.postservice.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractPublisher<LikeEvent> {

    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper,
                              @Value("${spring.data.redis.channels.notification_of_like_channel.name}")
                              String chanelName) {
        super(redisTemplate, objectMapper, chanelName);
    }
}