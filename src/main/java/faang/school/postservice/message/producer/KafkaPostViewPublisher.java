package faang.school.postservice.message.producer;

import faang.school.postservice.message.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostViewPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.views.name}")
    private String postViewsTopic;

    public void publishPostViewEvent(PostViewEvent postViewEvent) {
        log.info("Sending post view event {} into topic {}", postViewEvent, postViewsTopic);
        kafkaTemplate.send(postViewsTopic, postViewEvent);
    }
}
