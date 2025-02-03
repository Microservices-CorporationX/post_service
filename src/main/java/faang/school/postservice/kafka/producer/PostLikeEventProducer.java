package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.PostLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostLikeEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.post-like}")
    private String likeTopic;

    public void send(PostLikeEvent event) {
        kafkaTemplate.send(likeTopic, event);
    }
}
