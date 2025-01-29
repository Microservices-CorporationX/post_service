package faang.school.postservice.producer;

import faang.school.postservice.event.PostLikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLikeProducer implements KafkaPublisher<PostLikeEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic likeKafkaTopic;

    @Override
    public void publish(PostLikeEvent event) {
        kafkaTemplate.send(likeKafkaTopic.name(), event);
        log.info("Like event was sent to Kafka topic {}: {} ", likeKafkaTopic.name(), event);
    }
}
