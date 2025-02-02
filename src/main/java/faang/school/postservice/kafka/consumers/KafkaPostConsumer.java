package faang.school.postservice.kafka.consumers;

import faang.school.postservice.kafka.kafka_events_dtos.PostKafkaEventDto;
import faang.school.postservice.service.news_feed_service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final NewsFeedService newsFeedService;

    @KafkaListener(
            topics = "${spring.kafka.topics_names.post_topic}",
            groupId = "${spring.kafka.group_id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNewPost(PostKafkaEventDto postEvent, Acknowledgment ack) {
        try {
            postEvent.getAuthorFollowersIds().forEach(followerId ->
                    newsFeedService.addPostToNewsFeed(postEvent, followerId)
            );
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing Kafka event [{}]: {}", postEvent, e.getMessage(), e);
        }
    }
}