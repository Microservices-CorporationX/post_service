package faang.school.postservice.publisher.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userBanTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Object object) {
        String json;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to JSON: {}", object, e.getMessage());
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(userBanTopic.getTopic(), json);
    }
}
