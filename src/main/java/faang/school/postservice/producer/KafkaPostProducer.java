package faang.school.postservice.producer;

import faang.school.postservice.event.PostPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements KafkaPublisher<PostPublishedEvent> {
    @Value("${spring.kafka.channels.post-channel.name}")
    private String postChannel;

    private final KafkaTemplate<String, PostPublishedEvent> kafkaTemplate;

    @Override
    public void publish(PostPublishedEvent event) {
        try {
            SendResult<String, PostPublishedEvent> result = kafkaTemplate
                    .send(postChannel, String.valueOf(event.getPostId()), event).get();
            log.info("Topic: {}", result.getRecordMetadata().topic());
            log.info("Partition: {}", result.getRecordMetadata().partition());
            log.info("Offset: {}", result.getRecordMetadata().offset());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Message not send: {}",e.getMessage());
        }

        log.info("Post published event was sent to Kafka topic: {}", event.getPostId());
    }
}
