package faang.school.postservice.producers;

import faang.school.postservice.dto.CommentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaCommentProducerTest {

    @Mock
    private KafkaTemplate<String, CommentEvent> kafkaTemplate;

    @InjectMocks
    private KafkaCommentProducer kafkaCommentProducer;

    private static final String TEST_TOPIC = "comment-created-events-topic";
    private CommentEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new CommentEvent(1L, 2L, 3L, 4L, LocalDateTime.now());
    }

    @Test
    void testPublishEvent_Success() {
        CompletableFuture<SendResult<String, CommentEvent>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(kafkaTemplate.send(anyString(), any(CommentEvent.class))).thenReturn(future);

        kafkaCommentProducer.publishEvent(testEvent);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CommentEvent> eventCaptor = ArgumentCaptor.forClass(CommentEvent.class);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), eventCaptor.capture());

        assertEquals("comment-created-events-topic", topicCaptor.getValue());
        assertEquals(testEvent, eventCaptor.getValue());
    }

    @Test
    void publishEvent_Failure() {
        CompletableFuture<SendResult<String, CommentEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka failure"));

        when(kafkaTemplate.send(TEST_TOPIC, testEvent)).thenReturn(future);

        kafkaCommentProducer.publishEvent(testEvent);

        verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC), eq(testEvent));
    }
}
