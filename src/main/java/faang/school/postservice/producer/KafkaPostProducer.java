package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostViewKafkaEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostViewKafkaEvent> {

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic newTopic) {
        super(kafkaTemplate, newTopic);
    }

    @Override
    public void sendEvent(PostViewKafkaEvent event) {
        super.sendEvent(event);
    }

}
