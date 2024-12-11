package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> implements MessagePublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic channelTopic;

    public void publish(T event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic.getTopic(), json);
            log.info("Event published to Redis topic {}: {}", channelTopic.getTopic(), event);
        } catch (Exception e) {
            log.error("Failed to publish event to Redis", e);
            throw new RuntimeException("Failed to publish event to Redis", e);
        }
    }
}
