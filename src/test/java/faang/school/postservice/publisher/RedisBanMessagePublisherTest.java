package faang.school.postservice.publisher;

import faang.school.postservice.events.BanUserEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@ExtendWith(MockitoExtension.class)
class RedisBanMessagePublisherTest {
    @InjectMocks
    private RedisBanMessagePublisher redisBanMessagePublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic topic;

    private String strTopic = "topic";

    private BanUserEvent banUserEvent;
    @Test
    void testRedisMessagePublisherCallingRedisTemplate(){
        Mockito.when(topic.getTopic()).thenReturn(strTopic);
        redisBanMessagePublisher.publish(banUserEvent);

        Mockito.verify(topic).getTopic();
        Mockito.verify(redisTemplate).convertAndSend(topic.getTopic(),banUserEvent);
    }
}