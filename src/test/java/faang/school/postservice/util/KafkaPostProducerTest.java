package faang.school.postservice.util;

import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.kafka.KafkaPostProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class KafkaPostProducerTest {

    private KafkaTemplate<String, PostCreatedEvent> kafkaTemplate;
    private KafkaPostProducer kafkaPostProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        kafkaPostProducer = new KafkaPostProducer(kafkaTemplate);
    }

    @Test
    void sendPostCreatedEvent_ShouldSendEventToKafka() {
        Long postId = 1L;
        Long authorId = 123L;
        List<Long> subscriberIds = Arrays.asList(456L, 789L);

        kafkaPostProducer.sendPostCreatedEvent(postId, authorId, subscriberIds);

        ArgumentCaptor<PostCreatedEvent> eventCaptor = ArgumentCaptor.forClass(PostCreatedEvent.class);
        verify(kafkaTemplate, times(1)).send(eq("post-created"), eventCaptor.capture());

        PostCreatedEvent capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals(postId, capturedEvent.getPostId());
        assertEquals(authorId, capturedEvent.getAuthorId());
        assertEquals(subscriberIds, capturedEvent.getSubscriberIds());
        assertNotNull(capturedEvent.getCreatedAt());
    }
}
