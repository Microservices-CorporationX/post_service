package faang.school.postservice.kafka.publishers;

import faang.school.postservice.kafka.kafka_events_dtos.AbstractKafkaEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher {
    private final KafkaTemplate<String, AbstractKafkaEventDto> kafkaTemplate;
    private final NewTopic topic;

    protected void sendEvent(AbstractKafkaEventDto eventDto, String eventKey) {
        try {
            CompletableFuture<SendResult<String, AbstractKafkaEventDto>> future = kafkaTemplate.send(
                    topic.name(),
                    eventKey,
                    eventDto
            );

            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("Successfully sent event [{}] to topic [{}] at offset [{}] with key [{}]",
                            eventDto.getEventId(), topic.name(), result.getRecordMetadata().offset(), eventKey);
                } else {
                    log.error("Failed to send event [{}] due to error", eventDto, exception);
                }
            });

        } catch (Exception e) {
            log.error("Error processing event [{}]: {}", eventDto, e.getMessage(), e);
        }
    }
}