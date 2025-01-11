package faang.school.postservice.service.post.cache;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.redis.cache.PostCacheDto;
import faang.school.postservice.publisher.KafkaLikePublisher;
import faang.school.postservice.repository.cache.PostCacheRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {

    private final PostCacheRepository postCacheRepository;
    private final RedisTemplate<String, PostCacheDto> redisTemplate;
    private final KafkaLikePublisher kafkaLikePublisher;
    private final UserContext userContext;

    @Value("${spring.data.redis.post.time-to-live}")
    private int timeToLive;

    @Override
    public void savePost(PostCacheDto post) {
        String key = getKey(post.getId());
        postCacheRepository.save(post);
        redisTemplate.expire(key, Duration.ofMinutes(timeToLive));
    }

    @Override
    public Optional<PostCacheDto> getPost(Long id) {
        return postCacheRepository.findById(id);
    }

    @Retryable(retryFor = RedisSystemException.class,
            backoff = @Backoff(delayExpression = "${retryable.delay}"))
    @Override
    public void addLike(Long postId, Long likeId) {
        String key = getKey(postId);
        long userId = userContext.getUserId();
        redisTemplate.watch(key);
        try {
            redisTemplate.multi();
            PostCacheDto post = getPost(postId)
                    .orElseThrow(() -> new EntityNotFoundException("Post %d not found in cache: ".formatted(postId)));
            post.setLikesCount(post.getLikesCount() + 1);
            post.getLikes().add(likeId);
            postCacheRepository.save(post);
            kafkaLikePublisher.publishLikeEvent(post.getAuthorId(), userId, post.getId());
            redisTemplate.exec();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            redisTemplate.discard();
            throw new RedisSystemException("Add like transaction failed, will retry", e);
        } finally {
            redisTemplate.unwatch();
        }
    }

    @Retryable(retryFor = RedisSystemException.class,
            backoff = @Backoff(delayExpression = "${retryable.delay}"))
    @Override
    public void addComment(Long postId, Long commentId) {
        String key = getKey(postId);
        redisTemplate.watch(key);
        try {
            redisTemplate.multi();
            PostCacheDto post = getPost(postId)
                    .orElseThrow(() -> new EntityNotFoundException("Post %d not found in cache: ".formatted(postId)));
            post.setCommentsCount(post.getCommentsCount() + 1);
            post.getComments().add(commentId);
            postCacheRepository.save(post);
            redisTemplate.exec();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            redisTemplate.discard();
            throw new RedisSystemException("Add comment transaction failed, will retry", e);
        } finally {
            redisTemplate.unwatch();
        }

    }

    private String getKey(Long id) {
        return "post:" + id;
    }
}
