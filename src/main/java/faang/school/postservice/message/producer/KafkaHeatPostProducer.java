package faang.school.postservice.message.producer;

import faang.school.postservice.message.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaHeatPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.heat.name}")
    private String postsHeatTopic;

    public void publishPostEvents(PostEvent postEvent) {
        log.info("Sending post event {} into topic {}", postEvent, postsHeatTopic);
        kafkaTemplate.send(postsHeatTopic, postEvent);
    }
}
