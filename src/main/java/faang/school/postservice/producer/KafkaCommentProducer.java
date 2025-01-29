package faang.school.postservice.producer;

import faang.school.postservice.event.CommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractEventProducer<CommentEvent> {
    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic topic) {
        super(kafkaTemplate, topic);
    }

    @Override
    public void sendEvent(CommentEvent event) {
        super.sendEvent(event);
    }
}
