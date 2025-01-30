package faang.school.postservice.message.producer;

import faang.school.postservice.message.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.posts.name}")
    private String postsTopic;

    public void publishPostEvents(List<PostEvent> postEvents) {
        log.info("Sending post events {} into topic {}", postEvents, postsTopic);
        postEvents.forEach(postEvent -> kafkaTemplate.send(postsTopic, postEvent));
    }
}
