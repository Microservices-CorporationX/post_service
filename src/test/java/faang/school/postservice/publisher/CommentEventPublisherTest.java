package faang.school.postservice.publisher;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.event.CommentEvent;
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
class CommentEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private CommentEventPublisher commentEventPublisher;

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

        CommentEvent event = new CommentEvent();

        commentEventPublisher = new CommentEventPublisher(redisTemplate, redisProperties);

        commentEventPublisher.publish(event);

        verify(redisTemplate, times(1)).
                convertAndSend(redisProperties.channel().commentsChannel(), event);
    }
}