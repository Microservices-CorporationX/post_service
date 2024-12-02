package faang.school.postservice.producer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private NewTopic topic;

    public void sendEvent(T event) {
        log.info("Sending Json event: {} to Kafka topic: {}", event, topic.name());
        kafkaTemplate.send(topic.name(), event);
    }
}
