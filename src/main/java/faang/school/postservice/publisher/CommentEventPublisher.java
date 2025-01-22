package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.Event;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentTopic;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void publishMessage(Event event) {
        String json = objectMapper.writeValueAsString(event);
        redisTemplate.convertAndSend(commentTopic.getTopic(), json);
    }
}
