package faang.school.postservice.kafka_consumers;

import faang.school.postservice.dto.kafka_events.PostKafkaEventDto;
import faang.school.postservice.service.cache.NewsFeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final NewsFeedCacheService newsFeedCacheService;

    @KafkaListener(
            topics = "${spring.kafka.topics_names}",
            groupId = "${spring.kafka.group_id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNewPost(PostKafkaEventDto postEvent, Acknowledgment ack) {
        try {
            postEvent.getAuthorFollowersIds().forEach(followerId ->
                    newsFeedCacheService.addPostToNewsFeed(postEvent, followerId)
            );
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing Kafka event [{}]: {}", postEvent, e.getMessage(), e);
        }
    }
}