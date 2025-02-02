package faang.school.postservice.util;

import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.producer.KafkaPostProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPostProducerTest {

    private static final String TOPIC_NAME = "test-topic";
    private static final Long POST_ID = 1L;
    private static final Long AUTHOR_ID = 123L;
    private static final List<Long> SUBSCRIBER_IDS = Arrays.asList(456L, 789L);

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaPostProducer kafkaPostProducer;

    @Captor
    private ArgumentCaptor<PostCreatedEvent> eventCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaPostProducer, "topicName", TOPIC_NAME);
    }

    @Test
    void sendPostCreatedEventSuccessTest() {
        SendResult<String, Object> sendResult = createSuccessfulSendResult();
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(sendResult);
        when(kafkaTemplate.send(eq(TOPIC_NAME), any(PostCreatedEvent.class))).thenReturn(future);

        kafkaPostProducer.sendPostCreatedEvent(POST_ID, AUTHOR_ID, SUBSCRIBER_IDS);

        verify(kafkaTemplate).send(eq(TOPIC_NAME), eventCaptor.capture());
        PostCreatedEvent capturedEvent = eventCaptor.getValue();

        assertAll(
                () -> assertEquals(POST_ID, capturedEvent.getPostId()),
                () -> assertEquals(AUTHOR_ID, capturedEvent.getAuthorId()),
                () -> assertEquals(SUBSCRIBER_IDS, capturedEvent.getSubscriberIds()),
                () -> assertNotNull(capturedEvent.getCreatedAt())
        );
    }

    @Test
    void sendPostCreatedEventFailTest() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka send failed"));
        when(kafkaTemplate.send(eq(TOPIC_NAME), any(PostCreatedEvent.class))).thenReturn(future);

        kafkaPostProducer.sendPostCreatedEvent(POST_ID, AUTHOR_ID, SUBSCRIBER_IDS);

        verify(kafkaTemplate).send(eq(TOPIC_NAME), eventCaptor.capture());
        PostCreatedEvent capturedEvent = eventCaptor.getValue();

        assertAll(
                () -> assertEquals(POST_ID, capturedEvent.getPostId()),
                () -> assertEquals(AUTHOR_ID, capturedEvent.getAuthorId()),
                () -> assertEquals(SUBSCRIBER_IDS, capturedEvent.getSubscriberIds()),
                () -> assertNotNull(capturedEvent.getCreatedAt())
        );
    }

    @Test
    void sendPostCreatedEvent_WithEmptySubscriberList_ShouldSendEvent() {
        List<Long> emptySubscriberIds = List.of();
        SendResult<String, Object> sendResult = createSuccessfulSendResult();
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(sendResult);
        when(kafkaTemplate.send(eq(TOPIC_NAME), any(PostCreatedEvent.class))).thenReturn(future);

        kafkaPostProducer.sendPostCreatedEvent(POST_ID, AUTHOR_ID, emptySubscriberIds);

        verify(kafkaTemplate).send(eq(TOPIC_NAME), eventCaptor.capture());
        PostCreatedEvent capturedEvent = eventCaptor.getValue();

        assertAll(
                () -> assertEquals(POST_ID, capturedEvent.getPostId()),
                () -> assertEquals(AUTHOR_ID, capturedEvent.getAuthorId()),
                () -> assertTrue(capturedEvent.getSubscriberIds().isEmpty()),
                () -> assertNotNull(capturedEvent.getCreatedAt())
        );
    }

    private SendResult<String, Object> createSuccessfulSendResult() {
        TopicPartition topicPartition = new TopicPartition(TOPIC_NAME, 0);
        RecordMetadata recordMetadata = new RecordMetadata(
                topicPartition, 0, 0, System.currentTimeMillis(), 0L, 0, 0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(TOPIC_NAME, new PostCreatedEvent());
        return new SendResult<>(producerRecord, recordMetadata);
    }
}