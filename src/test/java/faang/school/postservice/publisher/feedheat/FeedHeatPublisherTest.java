package faang.school.postservice.publisher.feedheat;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feedheat.FeedHeatEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeedHeatPublisherTest {
    private static final String TOPIC_NAME = "topic";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Spy
    private ObjectMapper objectMapper;
    @InjectMocks
    private FeedHeatPublisher feedHeatPublisher;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(feedHeatPublisher, "topicName", TOPIC_NAME);
    }

    @Test
    void testPublish() throws Exception {
        FeedHeatEvent event = new FeedHeatEvent(List.of(1L, 2L, 3L));

        feedHeatPublisher.publish(event);

        verify(kafkaTemplate).send(TOPIC_NAME, objectMapper.writeValueAsString(event));
    }
}