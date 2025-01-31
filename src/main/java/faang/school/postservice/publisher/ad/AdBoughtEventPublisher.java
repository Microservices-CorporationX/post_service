package faang.school.postservice.publisher.ad;

import faang.school.postservice.dto.analytics.AnalyticsEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdBoughtEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.ad_bought_channel.name}")
    private String topic;

    public void publish(AnalyticsEventDto adBoughtEvent) {
        log.info("Publishing event: {}", adBoughtEvent);
        redisTemplate.convertAndSend(topic, adBoughtEvent);
    }
}
