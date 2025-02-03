package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.PostPublishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostPublishedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.post-published}")
    private String postPublishedTopic;

    public void send(PostPublishedEvent event) {
        kafkaTemplate.send(postPublishedTopic, event);
    }
}
