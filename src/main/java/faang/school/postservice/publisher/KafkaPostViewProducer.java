package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.KafkaPostViewDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer implements EventPublisher<KafkaPostViewDto> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic topicPostViews;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(KafkaPostViewDto event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicPostViews.name(), message);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
