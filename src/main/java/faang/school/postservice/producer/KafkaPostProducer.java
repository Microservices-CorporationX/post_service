package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostProducer {

    @Value("${spring.data.kafka.topics.posts}")
    private String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostEvent(PostEvent postEvent) {
        kafkaTemplate.send(topic, postEvent)
                .thenRunAsync(() -> log.info("Kafka send an event post with id: {}", postEvent.getId()));
    }

}

