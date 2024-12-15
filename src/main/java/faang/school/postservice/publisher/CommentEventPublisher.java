package faang.school.postservice.publisher;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher implements Publisher<CommentEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    @Retryable(
            retryFor = Exception.class,
            maxAttemptsExpression = "@retryProperties.maxAttempts",
            backoff = @Backoff(
                    delayExpression = "@retryProperties.maxDelay",
                    multiplierExpression = "@retryProperties.multiplier"
                    )
    )
    public void publish(Object event) {
        redisTemplate.convertAndSend(redisProperties.getChannel().getCommentChannel(), event);
    }

    @Override
    public Class<?> getEventClass() {
        return CommentEvent.class;
    }
}
