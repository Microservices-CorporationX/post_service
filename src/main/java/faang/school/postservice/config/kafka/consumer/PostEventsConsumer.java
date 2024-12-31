package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.event.PostFollowersEventRecord;
import faang.school.postservice.redis.service.FeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventsConsumer {
    private final FeedCacheService feedCacheService;

    @KafkaListener(topics = "${spring.data.kafka.topic_name.posts}", groupId = "${spring.data.kafka.consumer.group-id}")
    void listener(PostFollowersEventRecord event, Acknowledgment acknowledgment) {
        try {
            feedCacheService.addPostIdToAuthorFollowers(event.postId(), event.followersIds(), event.publishedAt());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Post with id:{} is not added to followers feeds.", event.postId());
            throw e;
        }
    }
}