package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.EventPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessagePublisher<T> implements MessagePublisher<T> {

    private final ObjectMapper objectMapper;
    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(T event) {
        objectMapper.findAndRegisterModules();
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
            log.info("The message published to Redis topic {}: {}", channelTopic.getTopic(), message);
        } catch (JsonProcessingException exception) {
            String errorMessage = "The message could not be serialized: " + event;
            log.error(errorMessage, exception);
            throw new EventPublishingException(errorMessage + " - exception: " + exception.getMessage());
        } catch (Exception exception) {
            String errorMessage = "Failed to publish the nessage to Redis: " + event;
            log.error(errorMessage, exception);
            throw new EventPublishingException(errorMessage + " - exception: " + exception.getMessage());
        }
    }
}
