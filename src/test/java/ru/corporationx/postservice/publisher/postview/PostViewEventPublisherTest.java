package ru.corporationx.postservice.publisher.postview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.corporationx.postservice.dto.analytics.AnalyticsEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import ru.corporationx.postservice.publisher.postview.PostViewEventPublisher;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostViewEventPublisherTest {
    public static final long RECEIVER_ID = 1L;
    public static final long ACTOR_ID = 2L;

    @Value("${spring.data.redis.channels.post_view_channel.name}")
    private String topic;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private PostViewEventPublisher postViewEventPublisher;

    @Test
    public void testSendMethodIsCalled() throws JsonProcessingException {
        AnalyticsEventDto analyticsEventDto = getAnalyticsEventDto();
        when(objectMapper.writeValueAsString(analyticsEventDto)).thenReturn(analyticsEventDto.toString());

        postViewEventPublisher.publish(analyticsEventDto);

        Mockito.verify(redisTemplate).convertAndSend(topic, analyticsEventDto.toString());
    }

    private static AnalyticsEventDto getAnalyticsEventDto() {
        return AnalyticsEventDto.builder()
                .receiverId(RECEIVER_ID)
                .actorId(ACTOR_ID)
                .build();
    }
}