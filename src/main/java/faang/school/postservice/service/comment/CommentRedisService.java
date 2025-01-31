package faang.school.postservice.service.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.post.PostRedisEntity;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.TreeSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentRedisService {
    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final TransactionTemplate transactionTemplate;

    @Value("${spring.data.redis.collections.posts.number-of-comments}")
    private int commentsNumber;

    public void updatePostInRedis(String redisKey, CommentEvent newComment, String cachedPost,
                                  RedisOperations<String, Object> operations) {
        try {
            PostRedisEntity postRedisEntity = objectMapper.readValue(cachedPost, PostRedisEntity.class);
            TreeSet<CommentEvent> comments = postRedisEntity.getComments();
            comments.add(newComment);
            if (comments.size() > commentsNumber) {
                comments.pollLast();
            }
            operations.opsForValue().set(redisKey, objectMapper.writeValueAsString(postRedisEntity));
        } catch (JsonProcessingException e) {
            log.error("""
                    Conversion error during message processing.
                    Method: updatePostInRedis.
                    Message: {}.
                    """, cachedPost, e);
            throw new RuntimeException(e);
        }
    }

    public void createPostInRedis(String redisKey, CommentEvent newComment,
                                  RedisOperations<String, Object> operations) {
        try {
            PostRedisEntity postRedisEntity = postRepository.findPostAsRedisEntityById(newComment.getPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Post doesn't exist."));

            transactionTemplate.executeWithoutResult(status -> {
                List<CommentEvent> latestComments = commentRepository
                        .findLatestByPostId(postRedisEntity.getPostId(), commentsNumber)
                        .stream()
                        .map(commentMapper::toEvent)
                        .toList();
                TreeSet<CommentEvent> comments = new TreeSet<>(latestComments);
                postRedisEntity.setComments(comments);
            });

            operations.opsForValue().set(redisKey, objectMapper.writeValueAsString(postRedisEntity));
        } catch (JsonProcessingException e) {
            log.error("""
                    Conversion error during message processing.
                    Method: createPostInRedis.
                    Redis key: {}.
                    """, redisKey, e);
            throw new RuntimeException(e);
        }
    }
}