package faang.school.postservice.publisher;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventPublisher implements Publisher<LikeEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(LikeEvent event) {
        log.info("Publishing like event: {} to channel: {}", event, redisProperties.channel().likesChannel());
        redisTemplate.convertAndSend(redisProperties.channel().likesChannel(), event);
    }

    @Override
    public Class<LikeEvent> getEventClass() {
        return LikeEvent.class;
    }
}
