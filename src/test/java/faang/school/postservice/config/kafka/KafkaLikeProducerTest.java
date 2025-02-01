package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.LikeEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class KafkaLikeProducerTest {

    @InjectMocks
    private KafkaLikeProducer kafkaLikeProducer;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setup() {
        kafkaLikeProducer.setNameTopic("likes");
    }

    @Test
    void testsendMessage() {
        LikeEvent event = new LikeEvent(1L, 2L, 3L, LocalDateTime.now());
        RecordMetadata recordMetadata = new RecordMetadata(
                new TopicPartition("likes", 0),
                0L,
                0L,
                0L,
                0L,
                0,
                0
        );
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>("likes", event);
        CompletableFuture<SendResult<String, Object>> completableFuture = new CompletableFuture<>();
        CompletableFuture.completedFuture(new SendResult<>(producerRecord, recordMetadata));
        when(kafkaTemplate.send(eq("likes"), eq(event)))
                .thenReturn(completableFuture);
        kafkaLikeProducer.sendMessage(event);
        verify(kafkaTemplate).send(eq("likes"), eq(event));
    }
}