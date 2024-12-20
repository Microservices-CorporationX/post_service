package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.like-event-topic}")
    private String likeEventTopic;

    public void publish(LikeEvent event) {
        redisTemplate.convertAndSend(likeEventTopic, event);
    }
}