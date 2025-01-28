package faang.school.postservice.producers;

import faang.school.postservice.dto.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer {

    private static final String TOPIC = "comment-created-events-topic";
    private final KafkaTemplate<String, CommentEvent> kafkaTemplate;

    public void publishEvent(CommentEvent commentEvent) {
        CompletableFuture<SendResult<String, CommentEvent>> future = kafkaTemplate
                .send(TOPIC, commentEvent);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Failed to send message: {}", exception.getMessage());
            } else {
                log.debug("Topic: {} Partition: {} Offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }

        });

        log.debug("Return createComment: {}", commentEvent);
    }
}
