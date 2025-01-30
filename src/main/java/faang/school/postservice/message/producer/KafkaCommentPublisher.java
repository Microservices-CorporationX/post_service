package faang.school.postservice.message.producer;

import faang.school.postservice.message.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.comments.name}")
    private String commentsTopic;

    public void publishCommentEvent(CommentEvent commentEvent) {
        log.info("Sending comment event {} into topic {}", commentEvent, commentsTopic);
        kafkaTemplate.send(commentsTopic, commentEvent);
    }
}
