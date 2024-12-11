package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.OutboxEvent;
import faang.school.postservice.publisher.Publisher;
import faang.school.postservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventProcessor {
    private final OutboxEventRepository outboxEventRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final List<Publisher<?>> publishers;

    @Scheduled(fixedRate = 60000)
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByProcessedFalse();

        for (OutboxEvent event : events) {
            try {
                Publisher<?> sender = publishers.stream()
                        .filter(publisher -> publisher.getEventClass().getSimpleName().equals(event.getEventType()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No publisher found for event type: " + event.getEventType()));

                sender.publish(objectMapper.readValue(event.getPayload(), sender.getEventClass()));

                event.setProcessed(true);
                outboxEventRepository.save(event);
            } catch (Exception e) {
                log.error("Error processing event: {}", event.getId(), e);
            }
        }
    }

    @Scheduled(cron = "${post.clean-outbox.scheduler.cron}")
    public void cleanProcessedEvents() {
        log.info("Starting cleanup of processed events");
        int deletedCount = outboxEventRepository.deleteProcessedEvents();
        log.info("Cleanup completed. Deleted {} processed events", deletedCount);
    }
}
