package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher implements Publisher<CommentEvent> {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.comments_event_channel.name}")
    private String channel;

    @Override
    public void publish(Object event) {
        log.info("Publishing comment event: {} to channel: {}", event, channel);
        redisTemplate.convertAndSend(channel, event);
    }

    @Override
    public Class<CommentEvent> getEventClass() {
        return CommentEvent.class;
    }
}
