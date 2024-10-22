package faang.school.postservice.service.publisher;

import faang.school.postservice.dto.event.LikePostEvent;
import faang.school.postservice.publisher.like.LikePostEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikePostEventPublisherTest {

    @InjectMocks
    private LikePostEventPublisher likePostEventPublisher;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    private static final String LIKE_POST_EVENT_TOPIC = "LikePostEventTopic";

    @Test
    @DisplayName("Успешная отправка message")
    public void whenPublishEventShouldSuccess() {
        LikePostEvent event = LikePostEvent.builder().build();
        when(channelTopic.getTopic()).thenReturn(LIKE_POST_EVENT_TOPIC);

        likePostEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(LIKE_POST_EVENT_TOPIC, event);
    }
}