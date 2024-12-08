package faang.school.postservice.event.publisher;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(LikeEvent event) {
        redisTemplate.convertAndSend("likeEventTopic", event);
    }
}
