package faang.school.postservice.producer;

import faang.school.postservice.event.CommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaCommentProducerTest {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private NewTopic commentEventsTopic;

    @InjectMocks
    private KafkaCommentProducer kafkaCommentProducer;
    private final String topic = "comments-topic";

    @BeforeEach
    void setUp() {
        when(commentEventsTopic.name()).thenReturn(topic);
    }

    @Test
    void send_SuccessTest() {
        CommentEvent event = getCommentEvent();

        kafkaCommentProducer.send(event);

        verify(kafkaTemplate).send(eq(topic), eq(event));
    }

    @Test
    void send_WhenKafkaTemplateThrowsException_FailTest() {
        CommentEvent event = getCommentEvent();

        when(kafkaTemplate.send(eq(topic), eq(event)))
                .thenThrow(new RuntimeException("Kafka send error"));

        assertThrows(RuntimeException.class, () -> {
            kafkaCommentProducer.send(event);
        });

        verify(kafkaTemplate).send(topic, event);
    }


    private CommentEvent getCommentEvent() {
        return CommentEvent.builder()
                .id(1L)
                .authorId(123L)
                .postId(456L)
                .content("Test comment content")
                .updatedAt(LocalDateTime.now())
                .build();
    }
}