package faang.school.postservice.message.producer;

import faang.school.postservice.message.event.UsersBanEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersBanPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic usersBanTopic;

    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2.0))
    public void publish(UsersBanEvent usersBanEvent) {
        log.info("Uploading an event to ban users: {}", usersBanEvent);
        redisTemplate.convertAndSend(usersBanTopic.getTopic(), usersBanEvent);
    }
}
