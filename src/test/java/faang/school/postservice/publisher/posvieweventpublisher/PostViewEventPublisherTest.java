package faang.school.postservice.publisher.posvieweventpublisher;

import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.publisher.PostViewEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostViewEventPublisherTest {

    private static final String TOPIC_NAME = "post-view-topic";
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic postViewTopic;
    @InjectMocks
    private PostViewEventPublisher postViewEventPublisher;

    @Test
    public void publishSuccessTest() {
        PostViewEvent postViewEvent = new PostViewEvent(1L, 2, 3, LocalDateTime.now());
        when(postViewTopic.getTopic()).thenReturn(TOPIC_NAME);
        assertDoesNotThrow(() -> postViewEventPublisher.publish(postViewEvent));
        verify(redisTemplate).convertAndSend(TOPIC_NAME, postViewEvent);
    }

}
