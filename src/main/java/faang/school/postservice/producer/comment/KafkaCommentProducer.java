package faang.school.postservice.producer.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.kafka.topics.comments.name}")
    private String topicName;

    public void sendMessage(CommentEvent event) {
        try {
            log.info("Event publication: {}", event);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicName, json);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to json. Comment event: {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
