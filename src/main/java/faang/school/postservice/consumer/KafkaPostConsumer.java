package faang.school.postservice.consumer;

import faang.school.postservice.events.PostEvent;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.posts-topic}",
            groupId = "${spring.kafka.listener.topics.posts-topic.group-id}")
    public void listenerPostsTopic(PostEvent postEvent) {
        for(Long subscriberId: postEvent.getSubscribersId()) {
            postCacheService.addPostToFeed(subscriberId, postEvent.getPostId());
        }
    }
}
