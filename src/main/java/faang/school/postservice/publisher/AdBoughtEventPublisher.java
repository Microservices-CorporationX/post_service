package faang.school.postservice.publisher;

import faang.school.postservice.config.redis.RedisConfigProperties;
import faang.school.postservice.event.AdBoughtEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdBoughtEventPublisher implements Publisher<AdBoughtEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConfigProperties redisConfigProperties;


    @Override
    @Retryable(retryFor = Exception.class,
            maxAttemptsExpression = "#{@retryProperties.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@retryProperties.initialDelay}",
                    multiplierExpression = "#{@retryProperties.multiplier}",
                    maxDelayExpression = "#{@retryProperties.maxDelay}"
            )
    )
    public void publish(AdBoughtEvent event) {
        String channel = redisConfigProperties.channel().ad_bought();
        redisTemplate.convertAndSend(channel, event);
    }

    @Override
    public Class<AdBoughtEvent> getEventClass() {
        return AdBoughtEvent.class;
    }
}
