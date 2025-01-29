package faang.school.postservice.publisher.feedheat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feedheat.FeedHeatEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeatPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.heat-feed-cache.name}")
    private String topicName;

    public void publish(FeedHeatEvent feedHeatEvent) {
        try {
            kafkaTemplate.send(topicName, objectMapper.writeValueAsString(feedHeatEvent));
        } catch (JsonProcessingException e) {
            log.error("Exception when converting FeedHeatEvent to json", e);
            throw new IllegalStateException("Exception when converting FeedHeatEvent to json");
        }
    }
}
