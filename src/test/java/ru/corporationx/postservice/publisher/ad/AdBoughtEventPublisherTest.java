package ru.corporationx.postservice.publisher.ad;

import ru.corporationx.postservice.dto.analytics.AnalyticsEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import ru.corporationx.postservice.publisher.ad.AdBoughtEventPublisher;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdBoughtEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channels.ad_bought_channel.name}")
    private String topic;
    @InjectMocks
    private AdBoughtEventPublisher adBoughtEventPublisher;

    @Test
    void testSendMethodIsCalled() {
        AnalyticsEventDto analyticsEventDto =
                new AnalyticsEventDto(1L, 1L, -1, LocalDateTime.now());
        adBoughtEventPublisher.publish(analyticsEventDto);
        verify(redisTemplate).convertAndSend(topic, analyticsEventDto);
    }
}