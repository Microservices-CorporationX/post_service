package faang.school.postservice.publisher.postpublish;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.event.PublishPostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class PublishPostEventPublisher {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.post-publish.name}")
    private String postPublishTopicName;

    @Value("${kafka.topics.post-publish-batched.name}")
    private String postPublishBatchTopicName;

    public void publish(PublishPostEvent event) {
        publish(event, false);
    }

    public void publish(PublishPostEvent event, boolean isBatched) {
        String topicName = isBatched ? postPublishBatchTopicName : postPublishTopicName;
        try {
            log.info("Publishing event to topic {}, event: {}", topicName, event);
            kafkaTemplate.send(topicName, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Exception when converting PublishPostEvent to json", e);
            throw new IllegalStateException("Exception when converting PublishPostEvent to json");
        }
    }
}
