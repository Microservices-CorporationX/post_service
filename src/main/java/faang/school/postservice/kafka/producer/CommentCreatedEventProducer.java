package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.CommentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.comment-created}")
    private String commentCreatedTopic;

    public void send(CommentCreatedEvent event) {
        kafkaTemplate.send(commentCreatedTopic, event);
    }
}
