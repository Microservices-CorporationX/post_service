package faang.school.postservice.publisher.post;

import faang.school.postservice.dto.event.PostFeedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventProducer extends AbstractEventProducer<PostFeedEvent>{

    @Autowired
    public PostEventProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic postTopic) {
        super(kafkaTemplate, postTopic);
    }
}
