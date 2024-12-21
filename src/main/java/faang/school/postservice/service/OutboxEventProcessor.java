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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventProcessor {
    private final OutboxEventRepository outboxEventRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final List<Publisher<?>> publishers;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private void processOutboxEvents() {
        try {
            while (true) {
                List<OutboxEvent> events = outboxEventRepository.findByProcessedFalse();

                if (events.isEmpty()) {
                    break;
                }

                for (OutboxEvent event : events) {
                    try {
                        Publisher<?> sender = publishers.stream()
                                .filter(publisher -> publisher.getEventClass().getSimpleName().equals(event.getEventType()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("No publisher found for event type: " + event.getEventType()));

                        processEvent(sender, event);

                        event.setProcessed(true);
                        outboxEventRepository.save(event);
                    } catch (Exception e) {
                        log.error("Error processing event: {}", event.getId(), e);
                    }
                }
            }
        } finally {
            isProcessing.set(false);
        }
    }

    public void triggerProcessing() {
        if (isProcessing.compareAndSet(false, true)) {
            executorService.submit(this::processOutboxEvents);
        }
    }

    @Scheduled(cron = "${post.clean-outbox.scheduler.cron}")
    public void cleanProcessedEvents() {
        log.info("Starting cleanup of processed events");
        int deletedCount = outboxEventRepository.deleteProcessedEvents();
        log.info("Cleanup completed. Deleted {} processed events", deletedCount);
    }

    private <T> void processEvent(Publisher<T> publisher, OutboxEvent event) throws Exception {
        T payload = objectMapper.readValue(event.getPayload(), publisher.getEventClass());
        publisher.publish(payload);
    }
}
