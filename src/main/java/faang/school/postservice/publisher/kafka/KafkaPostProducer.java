package faang.school.postservice.publisher.kafka;

import faang.school.postservice.event.PostPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements KafkaPublisher<PostPublishedEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postPublishedEventsTopic;

    @Override
    public void publish(PostPublishedEvent event) {
        kafkaTemplate.send(postPublishedEventsTopic.name(), event);
        log.info("Post published event was sent to Kafka topic: {}", postPublishedEventsTopic.name());
    }
}
