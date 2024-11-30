package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.KafkaFeedHeaterDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaFeedHeaterConsumer {
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topic.feed_heater:feed-heater}")
    @Transactional
    @Async("feedHeaterExecutor")
    public void listener(KafkaFeedHeaterDto event, Acknowledgment acknowledgment) {
        try {
            List<Long> userIds = event.getUserIds();
            userIds.forEach(this::createFeedForOneUser);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("FeedHeaterConsumer error");
            throw e;
        }
    }

    private void createFeedForOneUser(long userId) {
        feedService.getFeedByUserId(null, userId);
    }
}
