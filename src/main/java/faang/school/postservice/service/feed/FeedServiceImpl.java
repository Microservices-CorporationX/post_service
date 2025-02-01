package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.dto.comment.CommentRedis;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.dto.post.PostRedis;
import faang.school.postservice.exception.RedisTransactionFailedException;
import faang.school.postservice.service.redis.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RedisTemplate<String, Object> redisLettuceTemplate;
    private final RedisCacheService redisCacheService;

    @Value("${spring.data.redis.prefix-feeds:feeds}")
    private String prefixFeed;

    @Value("${spring.data.redis.max-feeds:500}")
    private int maxFeeds;

    @Value("${spring.data.redis.batch-posts:20}")
    private int batchPosts;

    @Value("${spring.data.redis.ttl-feeds:1}")
    private int ttl;

    private static final RedisScript<Long> FEED_UPDATE_SCRIPT = RedisScript.of(
            """
                    local key = KEYS[1]
                    local postId = ARGV[1]
                    local ttlSeconds = ARGV[2]
                    local maxFeeds = tonumber(ARGV[3])
                    local score = tonumber(ARGV[4])
                                        
                    redis.call('ZADD', key, score, postId)
                    redis.call('EXPIRE', key, ttlSeconds)
                                        
                    local count = redis.call('ZCARD', key)
                    if count > maxFeeds then
                        local removeCount = count - maxFeeds
                        redis.call('ZREMRANGEBYRANK', key, 0, removeCount - 1)
                    end
                                        
                    return 1
                    """,
            Long.class
    );

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = RedisTransactionFailedException.class
    )
    public void bindPostToFollower(Long followerId, Long postId) {
        String key = prefixFeed + followerId;
        long currentTime = System.currentTimeMillis();
        List<String> keys = Collections.singletonList(key);
        try {
            Long result = redisLettuceTemplate.execute(
                    FEED_UPDATE_SCRIPT,
                    keys,
                    postId,
                    ttl * 86400,
                    maxFeeds,
                    currentTime
            );
            if (result == null || result != 1) {
                throw new RedisTransactionFailedException(
                        "Failed to add post %s to feed %s".formatted(postId, key)
                );
            }
        } catch (Exception e) {
            log.error("Redis operation failed for post: {}", e.getMessage());
            throw new RedisTransactionFailedException(
                    "Redis operation failed for post %s".formatted(postId)
            );
        }
    }

    @Override
    public List<PostFeedDto> getPosts(Long postId, long userId) {
        int countPosts = (postId == null) ? batchPosts : 1;
        List<PostRedis> postsRedis = redisCacheService.findFeedByUserID(userId, countPosts);
        return postsRedis.stream()
                .map(post ->
                        PostFeedDto.builder()
                                .id(post.getId())
                                .content(post.getContent())
                                .author(redisCacheService.findUserById(post.getAuthorId()))
                                .likes(post.getLikes())
                                .views(post.getViews())
                                .comments(mapCommentsToDto(post.getComments()))
                                .build()
                )
                .toList();
    }

    private List<CommentFeedDto> mapCommentsToDto(List<CommentRedis> comments) {
        return comments.stream()
                .map(comment ->
                        CommentFeedDto.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .author(redisCacheService.findUserById(comment.getAuthorId()))
                                .createdAt(comment.getCreatedAt())
                                .build()
                )
                .collect(Collectors.toList());
    }
}
