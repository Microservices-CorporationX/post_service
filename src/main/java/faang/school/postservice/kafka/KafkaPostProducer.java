package faang.school.postservice.kafka;

import faang.school.postservice.dto.post.PostCreatedEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Log4j2
public class KafkaPostProducer {

    private final KafkaTemplate<String, PostCreatedEvent> kafkaTemplate;

    public KafkaPostProducer(KafkaTemplate<String, PostCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPostCreatedEvent(Long postId, Long authorId, List<Long> subscriberIds) {
        PostCreatedEvent event = new PostCreatedEvent(postId, authorId, subscriberIds, Instant.now());
        kafkaTemplate.send("post-created", event);
    }
}
