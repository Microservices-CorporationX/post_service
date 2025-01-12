package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.KafkaLikeDto;
import faang.school.postservice.service.post.PostCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaLikeConsumerTest {
    @Mock
    private PostCacheService postCacheService;
    @Mock
    private Acknowledgment acknowledgment;
    @InjectMocks
    private KafkaLikeConsumer kafkaLikeConsumer;

    private KafkaLikeDto event;

    @BeforeEach
    void setUp() {
        //Arrange
        event = new KafkaLikeDto(2L, 3L, 1L);
    }

    @Test
    void testKafkaLikeConsumerSuccess() {
        //Act
        kafkaLikeConsumer.listener(event, acknowledgment);

        //Assert
        verify(postCacheService, times(1))
                .incrementPostLikes(eq(3L), eq(2L));
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testKafkaLikeConsumerException() {
        //Arrange
        doThrow(new RuntimeException("Error"))
                .when(postCacheService).incrementPostLikes(anyLong(), anyLong());

        //Act
        Exception exception = assertThrows(RuntimeException.class,
                () -> kafkaLikeConsumer.listener(event, acknowledgment));

        //Assert
        assertEquals("Error", exception.getMessage());
        verify(acknowledgment, never()).acknowledge();
    }
}