package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeEventPublisherTest {
    @Mock
    private RedisTemplate<String, LikeEvent> redisTemplate;

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @BeforeEach
    void setUp() {
        try {
            var field = LikeEventPublisher.class.getDeclaredField("channel");
            field.setAccessible(true);
            field.set(likeEventPublisher, "testChannel");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void publishToChannel() {
        LikeEvent likeEvent = LikeEvent.builder()
                .likeAuthorId(1L)
                .postId(2L)
                .postAuthorId(3L)
                .build();

        likeEventPublisher.publish(likeEvent);

        verify(redisTemplate, times(1))
                .convertAndSend("testChannel", likeEvent);
    }
}