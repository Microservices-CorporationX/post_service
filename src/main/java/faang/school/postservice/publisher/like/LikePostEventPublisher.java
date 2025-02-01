package faang.school.postservice.publisher.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.event.LikePostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class LikePostEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.post-like.name}")
    private String postLikeTopicName;

    public void publish(LikePostEvent event) {
        try {
            kafkaTemplate.send(postLikeTopicName, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Exception when converting LikePostEvent to json", e);
            throw new IllegalStateException("Exception when converting LikePostEvent to json");
        }
    }
}
