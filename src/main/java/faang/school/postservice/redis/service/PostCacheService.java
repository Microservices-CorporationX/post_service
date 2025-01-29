package faang.school.postservice.redis.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.redis.entity.PostCache;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCacheService {
    private final PostCacheRedisRepository postCacheRedisRepository;
    private final PostCacheMapper postCacheMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostService postService;

    @Value("${spring.data.redis.post-cache.key-prefix}")
    private String postCacheKeyPrefix;

    @Value("${spring.data.redis.post-cache.views}")
    private String postCacheViews;

    @Value("${spring.data.redis.post-cache.likes}")
    private String postCacheLikes;

    @Value("${spring.data.redis.post-cache.comments}")
    private String postCacheComments;

    @Value("${spring.data.redis.post-cache.comment-end-index}")
    private long commentEndIndex;

    @Value("${spring.data.redis.post-cache.feed-end-index}")
    private String postCacheFeed;

    @Value("${spring.data.redis.post-cache.feed-end-index}")
    private long feedEndIndex;

    public void savePostCache(PostDto postDto) {
        PostCache postCache = postCacheMapper.toPostCache(postDto);
        postCacheRedisRepository.save(postCache);
    }

    public void incrementLikes(Long postId) {
        if (postCacheRedisRepository.existsById(postId)) {
            redisTemplate.opsForHash()
                    .increment(generateCachePostKey(postId), postCacheLikes, 1);
        } else {
            PostDto postDto = postService.getPostDto(postId);
            savePostCache(postDto);
        }
    }

    public void incrementViews(Long postId) {
        redisTemplate.opsForHash()
                .increment(generateCachePostKey(postId), postCacheViews, 1);
    }

    public void addComment(Long postId, CommentEvent commentEvent) {
        String postCommentKey = generateCachePostKey(postId) + postCacheComments;

        redisTemplate.opsForList().leftPush(postCommentKey, commentEvent);
        redisTemplate.opsForList().trim(postCommentKey, 0, commentEndIndex);
    }

    public void addPostToFeed(Long subscriberId, Long postId) {
        String feedKey = generateFeedKey(subscriberId);

        redisTemplate.opsForList().leftPush(feedKey, postId);
        redisTemplate.opsForList().trim(feedKey, 0, feedEndIndex);
    }

    private String generateCachePostKey(Long postId) {
        return postCacheKeyPrefix + postId;
    }

    private String generateFeedKey(Long subscriberId) {
        return postCacheFeed + subscriberId;
    }
}
