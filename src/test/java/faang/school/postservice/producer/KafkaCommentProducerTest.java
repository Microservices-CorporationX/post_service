package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.CommentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaCommentProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaCommentProducer producer;

    @Value("${spring.kafka.topic.comment}")
    private String commentsTopic;

    @Test
    public void testSuccessfulPublish() {
        CommentDto commentDto = prepareComment();

        producer.sendMessage(commentDto);

        verify(kafkaTemplate, times(1)).send(commentsTopic, commentDto);
        verify(kafkaTemplate).send(commentsTopic, commentDto);
    }

    private CommentDto prepareComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setPostId(2L);
        commentDto.setContent("content");
        commentDto.setCreatedAt(LocalDateTime.now());
        return commentDto;
    }
}