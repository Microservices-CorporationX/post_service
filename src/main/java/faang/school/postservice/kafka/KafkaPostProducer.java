package faang.school.postservice.kafka;

import faang.school.postservice.dto.post.PostCreatedEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Log4j2
public class KafkaPostProducer {

    @Value(value = "${spring.kafka.topicName}")
    private String topicName;

    private final KafkaTemplate<String, PostCreatedEvent> kafkaTemplate;

    public KafkaPostProducer(KafkaTemplate<String, PostCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPostCreatedEvent(Long postId, Long authorId, List<Long> subscriberIds) {
        PostCreatedEvent event = new PostCreatedEvent(postId, authorId, subscriberIds, Instant.now());
        kafkaTemplate.send(topicName, event);
    }
}
