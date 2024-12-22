package faang.school.postservice.publisher;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.event.AdBoughtEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdBoughtEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private AdBoughtEventPublisher adBoughtEventPublisher;

    private RedisProperties redisProperties;

    @Test
    @DisplayName("Event published success")
    void testPublish_Success() {
        redisProperties = new RedisProperties(
                "localhost",
                6379,
                new RedisProperties.Channel(
                        "adsChannel",
                        "calculationsChannel",
                        "likesChannel",
                        "userBansChannel",
                        "commentsChannel"));

        AdBoughtEvent event = new AdBoughtEvent();

        adBoughtEventPublisher = new AdBoughtEventPublisher(redisTemplate, redisProperties);

        adBoughtEventPublisher.publish(event);

        verify(redisTemplate, times(1)).
                convertAndSend(redisProperties.channel().adsChannel(), event);
    }
}