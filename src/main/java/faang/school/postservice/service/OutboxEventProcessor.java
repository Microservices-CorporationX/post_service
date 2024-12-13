package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.OutboxEvent;
import faang.school.postservice.publisher.Publisher;
import faang.school.postservice.repository.OutboxEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Service
public class OutboxEventProcessor {
    private final OutboxEventRepository outboxEventRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final List<Publisher<?>> publishers;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Map<String, Publisher<?>> publisherMap = new HashMap<>();

    public OutboxEventProcessor(OutboxEventRepository outboxEventRepository, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, List<Publisher<?>> publishers) {
        this.outboxEventRepository = outboxEventRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.publishers = publishers;

        for (Publisher<?> publisher : publishers) {
            publisherMap.put(publisher.getEventClass().getSimpleName(), publisher);
        }
    }

    public void processOutboxEvents() {
        try {
            while (true) {
                List<OutboxEvent> events = outboxEventRepository.findByProcessedFalse();

                if (events.isEmpty()) {
                    log.info("No more events to process");
                    break;
                }

                for (OutboxEvent event : events) {
                    try {
                        Publisher<?> sender = publisherMap.get(event.getEventType());
                        log.info("Processing event: {}", event.getId());
                        if (sender == null) {
                            log.error("No publisher found for event type: {}", event.getEventType());
                            throw new IllegalArgumentException("No publisher found for event type: " + event.getEventType());
                        }

                        sender.publish(objectMapper.readValue(event.getPayload(), sender.getEventClass()));
                        log.info("Event: {} processed successfully", event.getId());

                        event.setProcessed(true);

                        log.info("Saving processed event: {}", event.getId());
                        outboxEventRepository.save(event);
                    } catch (Exception e) {
                        log.error("Error processing event: {}", event.getId(), e);
                    }
                }
            }
        } finally {
            isProcessing.set(false);
            log.info("Outbox event processing completed");
        }
    }

    public void triggerProcessing() {
        if (isProcessing.compareAndSet(false, true)) {
            executorService.submit(this::processOutboxEvents);
            log.info("Outbox event processing started");
        }
    }

    @Scheduled(cron = "${post.clean-outbox.scheduler.cron}")
    public void cleanProcessedEvents() {
        log.info("Starting cleanup of processed events");
        int deletedCount = outboxEventRepository.deleteProcessedEvents();
        log.info("Cleanup completed. Deleted {} processed events", deletedCount);
    }
}
