package faang.school.postservice.consumer;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.like-topic}",
            groupId = "${spring.kafka.listener.topics.like-topic.group-id}")
    public void listenerLikesTopic (LikeEvent likeEvent, Acknowledgment acknowledgment) {

        postCacheService.incrementLikes(likeEvent.getPostId());
        acknowledgment.acknowledge();
    }
}
