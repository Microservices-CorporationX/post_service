package faang.school.postservice.publisher.postview;

import faang.school.postservice.dto.analytics.AnalyticsEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.post_view_channel.name}")
    private String topic;

    public void publish(AnalyticsEventDto postViewEvent) {
        redisTemplate.convertAndSend(topic, postViewEvent);
    }
}
