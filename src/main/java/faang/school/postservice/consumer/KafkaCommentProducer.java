package faang.school.postservice.consumer;

import faang.school.postservice.model.cache.CommentEvent;
import faang.school.postservice.model.cache.PostEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentProducer {
    private final RedisPostRepository redisPostRepository;

    @KafkaListener(topics = "${spring.data.kafka.topics.comment_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(CommentEvent commentEvent, Acknowledgment acknowledgment) {
        Optional<PostEvent> post = redisPostRepository.findById(commentEvent.getPostId());
        if (post.isPresent()) {
            post.get().setContent(commentEvent.getContent());
            redisPostRepository.save(post.get());
        }
        acknowledgment.acknowledge();
    }
}
