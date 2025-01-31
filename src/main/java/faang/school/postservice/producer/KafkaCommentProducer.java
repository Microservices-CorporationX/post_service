package faang.school.postservice.producer;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.events.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class KafkaCommentProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic-name.comment-topic}")
    private String commentTopic;

    public void sendCommentEvent(CommentEvent commentEvent) {
        kafkaTemplate.send(commentTopic, commentEvent);
    }
}
