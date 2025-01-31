package faang.school.postservice.consumer.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comment.CommentRedisService;
import faang.school.postservice.util.RedisTransactionExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final RedisTemplate<String, Object> redisTemplate;
    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;
    private final CommentRedisService commentRedisService;
    private final RedisTransactionExecutor redisTransactionExecutor;

    @KafkaListener(topics = "${spring.data.kafka.topics.comments.name}",
            groupId = "${spring.data.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message, Acknowledgment ack) {
        try {
            log.info("Starts processing the message: {}", message);
            CommentEvent newComment = objectMapper.readValue(message, CommentEvent.class);
            if (!commentRepository.existsById(newComment.getCommentId())) {
                ack.acknowledge();
                return;
            }

            String redisKey = "Post:" + newComment.getPostId();
            redisTransactionExecutor.executeRedisTransaction(redisTemplate, redisKey,
                    redisOperations -> {
                        String cachedPost = (String) redisOperations.opsForValue().get(redisKey);
                        if (cachedPost != null) {
                            commentRedisService.updatePostInRedis(redisKey, newComment, cachedPost, redisOperations);
                        } else {
                            commentRedisService.createPostInRedis(redisKey, newComment, redisOperations);
                        }
                    });
            ack.acknowledge();
            log.info("Successful completion of message processing: {}", message);
        } catch (JsonProcessingException e) {
            log.error("""
                    Conversion error during message processing.
                    Message: {}; acknowledgement: {}
                    """, message, ack, e);
            throw new RuntimeException(e);
        }
    }
}