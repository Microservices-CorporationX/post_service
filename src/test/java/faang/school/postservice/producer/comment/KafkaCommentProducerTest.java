package faang.school.postservice.producer.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaCommentProducerTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private KafkaCommentProducer commentProducer;

    @Test
    public void testSuccessfulSendMessage() throws JsonProcessingException {
        CommentEvent event = new CommentEvent();
        String json = "test json";
        String topicName = "comments";

        ReflectionTestUtils.setField(commentProducer, "topicName", topicName);
        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        commentProducer.sendMessage(event);

        verify(objectMapper).writeValueAsString(event);
        verify(kafkaTemplate).send(topicName, json);
    }

    @Test
    public void testSendMessageThrowsException() throws JsonProcessingException {
        CommentEvent event = new CommentEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(mock(JsonProcessingException.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> commentProducer.sendMessage(event));
        assertEquals("Error converting object to json.", thrown.getMessage());
    }
}