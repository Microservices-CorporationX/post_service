package faang.school.postservice.producer;

import faang.school.postservice.event.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaCommentProducer implements KafkaEventProducer<CommentEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic commentEventsTopic;

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate, @Qualifier(value = "commentsTopic") NewTopic commentEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.commentEventsTopic = commentEventsTopic;
    }

    @Override
    public void send(CommentEvent event) {
        kafkaTemplate.send(commentEventsTopic.name(), event);
        log.info("Comment event was sent to Kafka topic: {}", commentEventsTopic.name());
    }
}
