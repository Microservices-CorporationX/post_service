package faang.school.postservice.consumer.feedheat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feedheat.FeedHeatEvent;
import faang.school.postservice.service.feed.cache.HeaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FeedHeatConsumer {
    private final ObjectMapper objectMapper;
    private final HeaterService heaterService;

    @KafkaListener(topics = "${kafka.topics.heat-feed-cache.name}",
            groupId = "${kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message, Acknowledgment ack) {
        try {
            FeedHeatEvent feedHeatEvent = objectMapper.readValue(message, FeedHeatEvent.class);
            feedHeatEvent.getUserIds().forEach(heaterService::heatUser);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Could not deserialize FeedHeatEvent from json", e);
            throw new IllegalStateException("Could not deserialize FeedHeatEvent from json");
        }
    }
}
