package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(LikeEvent event) {
        redisTemplate.convertAndSend("likeEventTopic", event);
    }
}
