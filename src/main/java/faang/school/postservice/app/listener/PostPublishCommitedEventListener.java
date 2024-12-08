package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.AuthorPostKafkaProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.application.PostsPublishCommittedEvent;
import faang.school.postservice.model.event.kafka.AuthorPostKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostPublishCommitedEventListener {
    private final RedisPostService redisPostService;
    private final RedisPostDtoMapper redisPostDtoMapper;
    private final PostMapper postMapper;
    private final AuthorPostKafkaProducer authorPostKafkaProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostsPublishCommittedEvent(PostsPublishCommittedEvent event) {
        List<Post> posts = event.getPosts();
        log.info("Processing committed posts: {}", posts.size());

        posts.forEach(post -> {
            log.debug("Sending AuthorPublishedPostKafkaEvent for author with id = {} in Kafka for user-service",
                    post.getAuthorId());
            authorPostKafkaProducer.sendEvent(
                    new AuthorPostKafkaEvent(post.getId(), post.getAuthorId(), post.getPublishedAt()));

            log.debug("Saving post with id = {} in Redis if needed", post.getId());
            PostDto postDto = postMapper.toPostDto(post);
            RedisPostDto redisPostDto = redisPostDtoMapper.mapToRedisPostDto(postDto);
            redisPostService.savePostIfNotExists(redisPostDto);
        });
    }
}
