package faang.school.postservice.config.kafka;

import faang.school.postservice.cache_entities.PostCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostPublisher {
    private final KafkaTemplate<String, PostCache> kafkaTemplate;

    public void sendMessage(PostCache message) {
        CompletableFuture<SendResult<String, PostCache>> future = kafkaTemplate.send("posts", message.getPostId().toString(), message);
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Sent PostCache with ID [{}] to topic [{}] at offset [{}]",
                        message.getPostId(), "posts", result.getRecordMetadata().offset());
            } else {
                log.error("Sent message=[" + message + "] with error=[" + exception.getMessage() + "]");
            }
        });
    }
}