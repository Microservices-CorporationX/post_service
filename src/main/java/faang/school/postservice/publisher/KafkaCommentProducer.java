package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.KafkaCommentDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentProducer implements EventPublisher<KafkaCommentDto>{

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic topicComments;
    private final ObjectMapper objectMapper;
    @Override
    public void publish(KafkaCommentDto event) {
        try {
            String message =  objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicComments.name(), message);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
