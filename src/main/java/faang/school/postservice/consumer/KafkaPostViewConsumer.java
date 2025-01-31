package faang.school.postservice.consumer;

import faang.school.postservice.events.PostViewsEvent;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.views-topic}",
            groupId = "${spring.kafka.listener.topics.views-topic.group-id}")
    public void listenerViewsTopic(PostViewsEvent postViewsEvent, Acknowledgment acknowledgment) {
        postCacheService.incrementViews(postViewsEvent.getPostId());
        acknowledgment.acknowledge();
    }
}
