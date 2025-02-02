package faang.school.postservice.consumer;

import faang.school.postservice.model.cache.LikeCache;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final RedisPostRepository redisPostRepository;

    @KafkaListener(topics = "${spring.data.kafka.topics.like_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(LikeCache likeCache, Acknowledgment acknowledgment) {
        Optional<PostCache> post = redisPostRepository.findById(likeCache.getPostId());
        if (post.isPresent()) {
            int currentCountOfLikes = post.get().getCountOfLikes();
            post.get().setCountOfLikes(currentCountOfLikes + 1);

            redisPostRepository.save(post.get());
        }
        acknowledgment.acknowledge();
    }
}
