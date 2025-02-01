package faang.school.postservice.config.kafka.consumer;


import faang.school.postservice.model.cache.FeedCache;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final RedisFeedRepository redisFeedRepository;

    @KafkaListener(topics = "${spring.data.kafka.topics.post_topic}")
    public void listen(PostCache postCache, Acknowledgment acknowledgment) {
        postCache.getFollowersId().forEach(follower -> {
            FeedCache feed = new FeedCache();
            feed.setUserId(follower);
            feed.setPostsId(List.of(postCache.getId()));
            redisFeedRepository.save(feed);
        });
    }
}
