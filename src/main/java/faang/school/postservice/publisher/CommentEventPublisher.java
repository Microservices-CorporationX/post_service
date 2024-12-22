package faang.school.postservice.publisher;

import faang.school.postservice.config.redis.RedisConfigProperties;
import faang.school.postservice.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher implements Publisher<CommentEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConfigProperties redisConfigProperties;

    @Retryable(
            retryFor = Exception.class,
            maxAttemptsExpression = "@retryProperties.maxAttempts",
            backoff = @Backoff(
                    delayExpression = "@retryProperties.maxDelay",
                    multiplierExpression = "@retryProperties.multiplier"
                    )
    )
    @Override
    public void publish(CommentEvent event) {
        log.info("Publishing comment event: {} to channel: {}", event, redisConfigProperties.channel().comments_events());
        redisTemplate.convertAndSend(redisConfigProperties.channel().comments_events(), event);
    }

    @Override
    public Class<CommentEvent> getEventClass() {
        return CommentEvent.class;
    }
}
