package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.CommentLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentLikeEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.comment-like}")
    private String likeTopic;

    public void send(CommentLikeEvent event) {
        kafkaTemplate.send(likeTopic, event);
    }
}
