package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.KafkaFeedHeaterDto;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaFeedHeaterConsumerTest {
    @Mock
    private FeedService feedService;
    @Mock
    private Acknowledgment acknowledgment;
    @InjectMocks
    private KafkaFeedHeaterConsumer kafkaFeedHeaterConsumer;

    private KafkaFeedHeaterDto event;

    @BeforeEach
    void setUp() {
        //Arrange
        event = new KafkaFeedHeaterDto(List.of(1L, 2L, 3L));
    }

    @Test
    void testFeedHeaterConsumerSuccess() {
        //Act
        kafkaFeedHeaterConsumer.listener(event, acknowledgment);

        //Assert
        verify(feedService, times(1)).getFeedByUserId(1L);
        verify(feedService, times(1)).getFeedByUserId(2L);
        verify(feedService, times(1)).getFeedByUserId(3L);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testFeedHeaterConsumerException() {
        //Arrange
        doThrow(new RuntimeException("Error")).when(feedService).getFeedByUserId(2L);

        //Act
        assertThrows(RuntimeException.class, () -> kafkaFeedHeaterConsumer.listener(event, acknowledgment));

        //Assert
        verify(acknowledgment, never()).acknowledge();
        verify(feedService, times(1)).getFeedByUserId(1L);
    }
}