package faang.school.postservice.consumer;

import faang.school.postservice.model.cache.CommentEvent;
import faang.school.postservice.model.cache.PostEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentProducer {
    private final RedisPostRepository redisPostRepository;

    @Value("${feed.max-comment-size}")
    private int maxCommentSize;

    @KafkaListener(topics = "${spring.data.kafka.topics.comment_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(CommentEvent commentEvent, Acknowledgment acknowledgment) {
        boolean updated = false;

        while (!updated) {
            Optional<PostEvent> post = redisPostRepository.findById(commentEvent.getPostId());
            if (post.isPresent()) {
                try {
                    setNewComment(post.get(), commentEvent);
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

    private void setNewComment(PostEvent postEvent, CommentEvent comment) {
        if (postEvent.getComments() == null) {
            TreeSet<CommentEvent> comments = new TreeSet<>(Comparator.comparing(CommentEvent::getCreatedAt).reversed());
            comments.add(comment);
            postEvent.setComments(comments);
        } else {
            postEvent.getComments().add(comment);
            while (postEvent.getComments().size() > maxCommentSize) {
                postEvent.getComments().remove(postEvent.getComments().last());
            }
        }
        redisPostRepository.save(postEvent);
        log.debug("post {} update: added new comment ", postEvent.getId());
    }
}
