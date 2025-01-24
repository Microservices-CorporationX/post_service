package faang.school.postservice.consumer;

import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.redis.RedisPostRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaCommentConsumer {
    private final RedisPostRepository repository;
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${spring.data.redis.max-comment}")
    private int maxComments;

    @KafkaListener(topics = "${spring.data.kafka.topics.comments.name}", groupId = "${spring.data.kafka.consumer.group-id}")
    @Transactional
    public void consume(String message){
        log.info("Received message: {}", message);
        CommentRedis comment = parseComment(message);
        Long postId = comment.getPostId();

        lock.lock();
        try {
            Optional<PostRedis> optionalPostRedis = repository.findById(postId);
            if (optionalPostRedis.isPresent()){
                PostRedis postRedis = optionalPostRedis.get();
                TreeSet<CommentRedis> commentRedis = postRedis.getComments();

                if (commentRedis == null){
                    commentRedis = new TreeSet<>((c1, c2)->c1.getUpdateAt().compareTo(c2.getUpdateAt()));
                }
                commentRedis.add(comment);
                while (commentRedis.size()>maxComments){
                    commentRedis.pollFirst();
                }
                postRedis.setComments(commentRedis);
                repository.save(postRedis);

                log.info("Comment add to post {}: {}", postId, commentRedis);
            }else {
                log.warn("Post with ID {} not found", postId);
            }
        }finally {
            lock.unlock();
        }
    }

    private CommentRedis parseComment(String message) {
        // Implement parsing logic here
        return new CommentRedis();
    }

}
