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
public class LikeEventPublisher {
    private final RedisTemplate<String, LikeEvent> redisTemplate;

    @Value("${spring.data.redis.channels.events_channel.name}")
    private String channel;

    public void publishLikeEvent(LikeEvent event) {
        log.info("Publishing like event: {} to channel: {}", event, channel);
        redisTemplate.convertAndSend(channel, event);
    }
}
