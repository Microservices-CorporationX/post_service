package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.CommentPostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommentEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    PostCommentEventPublisher commentEventPublisher;

    private CommentPostEvent commentEvent;

    @BeforeEach
    public void setUp() {
        commentEvent = new CommentPostEvent(
                new Random().nextLong(1000),
                new Random().nextLong(1000),
                new Random().nextLong(1000),
                LocalDateTime.now());
    }

    @Test
    void publish_whenOk() throws JsonProcessingException {
        String json = "json";
        when(objectMapper.writeValueAsString(commentEvent)).thenReturn(json);
        commentEventPublisher.publish(commentEvent);
        Mockito.verify(redisTemplate, Mockito.times(1))
                .convertAndSend(Mockito.any(), Mockito.any());
        Mockito.verify(channelTopic, Mockito.times(2))
                .getTopic();
    }

    @Test
    @DisplayName("Should throw Exception for unexpected error")
    public void testPublishingEvent_thenGetUnexpectedError() {
        when(channelTopic.getTopic()).thenReturn("dummyTopic");
        doThrow(new RuntimeException("Test")).when(redisTemplate).convertAndSend(anyString(), anyString());
        assertThrows(RuntimeException.class, () -> commentEventPublisher.publish(commentEvent));
    }
}