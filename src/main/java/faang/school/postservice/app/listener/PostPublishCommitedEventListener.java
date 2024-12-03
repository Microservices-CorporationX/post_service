package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.PostKafkaProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.entity.UserShortInfo;
import faang.school.postservice.model.event.application.PostsPublishCommittedEvent;
import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import faang.school.postservice.service.RedisUserService;
import faang.school.postservice.service.UserShortInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostPublishCommitedEventListener {

    private static final int REFRESH_TIME_IN_HOURS = 3;

    @Value("${kafka.batch-size.follower:1000}")
    private int followerBatchSize;
    @Value("${system-user-id}")
    private int systemUserId;

    private final RedisPostService redisPostService;
    private final RedisPostDtoMapper redisPostDtoMapper;
    private final PostKafkaProducer postKafkaProducer;
    private final RedisUserService redisUserService;
    private final PostMapper postMapper;
    private final UserShortInfoService userShortInfoService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostsPublishCommittedEvent(PostsPublishCommittedEvent event) {
        List<Post> posts = event.getPosts();
        log.info("Processing committed posts: {}", posts.size());

        posts.forEach(post -> {
            log.debug("Saving author of post (user with id = {}) in DB and Redis", post.getAuthorId());
            UserShortInfo userShortInfo = userShortInfoService
                    .updateUserShortInfoIfStale(post.getAuthorId(), REFRESH_TIME_IN_HOURS);
            redisUserService.updateUserIfStale(userShortInfo, REFRESH_TIME_IN_HOURS);

            log.debug("Saving post with id = {} in Redis if needed", post.getId());
            PostDto postDto = postMapper.toPostDto(post);
            RedisPostDto redisPostDto = redisPostDtoMapper.mapToRedisPostDto(postDto);
            redisPostService.savePostIfNotExists(redisPostDto);

            log.debug("Start sending PostPublishedEvent for post with id = {} to Kafka", post.getId());
            List<Long> followerIds = redisUserService.getFollowerIds(post.getAuthorId());

            if (followerIds.isEmpty()) {
                return;
            }

            for (int indexFrom = 0; indexFrom < followerIds.size(); indexFrom += followerBatchSize) {
                int indexTo = Math.min(indexFrom + followerBatchSize, followerIds.size());
                PostPublishedKafkaEvent subEvent = new PostPublishedKafkaEvent(
                        postDto.getId(),
                        followerIds.subList(indexFrom, indexTo),
                        postDto.getPublishedAt());
                postKafkaProducer.sendEvent(subEvent);
            }
        });
    }
}
