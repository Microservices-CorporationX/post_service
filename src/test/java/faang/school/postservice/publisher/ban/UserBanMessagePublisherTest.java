package faang.school.postservice.publisher.ban;

import faang.school.postservice.event.ban.UserBanEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserBanMessagePublisherTest {
    private static final String TOPIC_NAME = "user-ban-topic";
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic userBanTopic;
    @InjectMocks
    private UserBanMessagePublisher userBanMessagePublisher;

    @Test
    void publishSuccessTest() {
        UserBanEvent userBanEvent = new UserBanEvent(1L);
        when(userBanTopic.getTopic()).thenReturn(TOPIC_NAME);
        assertDoesNotThrow(() -> userBanMessagePublisher.publish(userBanEvent));
        verify(redisTemplate).convertAndSend(TOPIC_NAME, userBanEvent);
    }
}
