package faang.school.postservice.kafka.consumers;

import faang.school.postservice.kafka.kafka_events_dtos.PostViewKafkaEventDto;
import faang.school.postservice.service.cache.PostCacheService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final PostService postService;
    private final PostCacheService postCacheService;

    @KafkaListener(
            topics = "${spring.kafka.topics_names.post_view_topic}",
            groupId = "${spring.kafka.group_id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePostView(PostViewKafkaEventDto viewEvent, Acknowledgment ack) {
        try {
            Long updatedPostViews = postService.incrementPostViews(viewEvent.getPostId());
            postCacheService.updateCountViews(viewEvent.getPostId(), updatedPostViews);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing Kafka event [{}]: {}", viewEvent, e.getMessage(), e);
        }
    }
}