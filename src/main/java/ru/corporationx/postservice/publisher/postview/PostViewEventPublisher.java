package ru.corporationx.postservice.publisher.postview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.corporationx.postservice.dto.analytics.AnalyticsEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.post_view_channel.name}")
    private String topic;

    public void publish(AnalyticsEventDto postViewEvent) {
        try {
            log.info("event publication: {}", postViewEvent);
            String stringValue = objectMapper.writeValueAsString(postViewEvent);
            redisTemplate.convertAndSend(topic, stringValue);
        } catch (JsonProcessingException e) {
            log.error("failed to serialize: {}", postViewEvent, e);
            throw new RuntimeException(e);
        }
    }
}
