package faang.school.postservice.producer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
@RequiredArgsConstructor
public class AbstractEventProducer<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private NewTopic topic;

    public void sendEvent(T event) {
        kafkaTemplate.send(topic.name(), event);
    }
}
