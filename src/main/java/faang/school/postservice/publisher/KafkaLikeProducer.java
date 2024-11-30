package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.KafkaLikeDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeProducer implements EventPublisher<KafkaLikeDto> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic topicLikes;

    @Override
    public void publish(KafkaLikeDto event) {
        kafkaTemplate.send(topicLikes.name(), event);
    }
}
