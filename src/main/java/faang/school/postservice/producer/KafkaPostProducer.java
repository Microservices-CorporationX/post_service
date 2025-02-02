package faang.school.postservice.producer;

import faang.school.postservice.dto.post.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class KafkaPostProducer {

    @Value(value = "${spring.data.kafka.topics.post-channel.name}")
    private String topicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostCreatedEvent(Long postId, Long authorId, List<Long> subscriberIds) {
        PostCreatedEvent event = new PostCreatedEvent(postId, authorId, subscriberIds, Instant.now());
        kafkaTemplate.send(topicName, event)
                .thenAccept(result -> log.info("Successfully sent event: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send event: {}", event, ex);
                    return null;
                });
    }
}