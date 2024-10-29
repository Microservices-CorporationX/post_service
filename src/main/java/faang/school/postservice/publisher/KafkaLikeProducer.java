package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.KafkaLikeDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeProducer implements EventPublisher<KafkaLikeDto> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic topicLikes;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(KafkaLikeDto event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicLikes.name(), message);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
