package faang.school.postservice.consumer;

import faang.school.postservice.model.cache.LikeEvent;
import faang.school.postservice.model.cache.PostEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final RedisPostRepository redisPostRepository;

    @KafkaListener(topics = "${spring.data.kafka.topics.like_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(LikeEvent likeEvent, Acknowledgment acknowledgment) {
        boolean updated = false;

        while (!updated) {
            Optional<PostEvent> post = redisPostRepository.findById(likeEvent.getPostId());
            if (post.isPresent()) {
                try {
                    int currentCountOfLikes = post.get().getCountOfLikes();
                    post.get().setCountOfLikes(currentCountOfLikes + 1);

                    redisPostRepository.save(post.get());
                    updated = true;
                } catch (OptimisticLockingFailureException e) {
                    log.warn("Optimistic lock exception occurred. Retrying...");
                }
            } else {
                updated = true;
            }
        }
        acknowledgment.acknowledge();
    }
}
