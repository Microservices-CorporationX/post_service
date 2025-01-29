package faang.school.postservice.consumer;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableKafka
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final RedisPostRepository redisPostRepository;

    @KafkaListener(topics = "comments", groupId = "post-service")
    public void listen(CommentDto commentDto, Acknowledgment ack) {
        long postId = commentDto.getPostId();

        PostDto post = redisPostRepository.getPost(postId);

        if (post != null) {
            post.getComments().add(commentDto);

            redisPostRepository.savePost(post);

            ack.acknowledge();
            log.info("Comment successfully added to post with ID: {}", postId);
        } else {
            log.warn("Post with ID: {} not found in Redis", postId);
        }
    }
}
