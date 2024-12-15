package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventPublisher implements Publisher<LikeEvent> {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.like-events}")
    private String channel;

    @Override
    public void publish(Object event) {
        log.info("Publishing like event: {} to channel: {}", event, channel);
        redisTemplate.convertAndSend(channel, event);
    }

    @Override
    public Class<LikeEvent> getEventClass() {
        return LikeEvent.class;
    }
}
