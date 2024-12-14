package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private CommentEventPublisher commentEventPublisher;

    private static final String TEST_CHANNEL = "comments_channel";

    @BeforeEach
    void setUp() {
        commentEventPublisher = new CommentEventPublisher(redisTemplate);
        ReflectionTestUtils.setField(commentEventPublisher, "channel", TEST_CHANNEL);
    }

    @Test
    void publishShouldSendEventToRedis() {
        CommentEvent event = CommentEvent.builder().commentId(1L).commentAuthorId(1L).postAuthorId(1L).postId(1L).build();

        commentEventPublisher.publish(event);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);

        verify(redisTemplate, times(1)).convertAndSend(channelCaptor.capture(), eventCaptor.capture());

        assertEquals(TEST_CHANNEL, channelCaptor.getValue());
        assertEquals(event, eventCaptor.getValue());
    }

    @Test
    void getEventClassShouldReturnCorrectClass() {
        Class<CommentEvent> eventClass = commentEventPublisher.getEventClass();

        assertEquals(CommentEvent.class, eventClass);
    }
}