package faang.school.postservice.publisher;

import faang.school.postservice.config.RetryProperties;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.event.CommentEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    @Mock
    private RetryProperties retryProperties;

    @InjectMocks
    private CommentEventPublisher commentEventPublisher;

    @Test
    @DisplayName("Event published success")
    void testPublish_Success() {
        RedisProperties.Channel channel = new RedisProperties.Channel();
        String channelName = "testChannel";
        channel.setCommentChannel(channelName);
        CommentEvent event = new CommentEvent();

        when(redisProperties.getChannel()).thenReturn(channel);

        commentEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(channelName, event);
    }

}