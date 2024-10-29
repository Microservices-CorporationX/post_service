package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.service.post.PostCacheService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.support.collections.RedisZSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final PostCacheService postCacheService;
    private final PostService postService;
    private final RedisFeedRepository redisFeedRepository;
    private final PostMapper mapper;
    @Value("${spring.data.redis.feed-cache.batch_size:20}")
    private int batchSize;
    @Value("${spring.data.redis.feed-cache.max_feed_size:500}")
    private int maxFeedSize;

    public List<PostDto> getFeedByUserId(Long postId, long userId) {
        List<Long> followerPostIds = getFollowerPostIds(userId, postId);
        List<PostDto> list = new ArrayList<>();
        if (followerPostIds.isEmpty()) {
            list.add(postService.getPost(postId));
        }
        list = postCacheService.getPostCacheByIds(followerPostIds).stream()
                .map(mapper::toDto)
                .toList();
        return list;
    }

    public void addPostIdToAuthorSubscribers(Long postId, List<Long> subscriberIds) {
        subscriberIds.forEach(subscriberId -> addPostIdToSubscriberFeed(postId, subscriberId));
    }

    private List<Long> getFollowerPostIds(Long userId, Long postId) {
        RedisFeed feed = redisFeedRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No feed for user"));
        RedisZSet<Long> posts = feed.getPostIds();
        if (postId == null) {
            return getFeedInRange(posts, 0, batchSize - 1);
        } else {
            Long rank = posts.rank(postId);
            if (rank == null) {
                return emptyList();
            }
            return getFeedInRange(posts, rank + 1, rank + batchSize);
        }
    }

    private List<Long> getFeedInRange(RedisZSet<Long> posts, long startPostId, long endPostId) {
        Set<Long> result = new HashSet<>();
        try {
            result = posts.range(startPostId, endPostId);
        } catch (ArrayIndexOutOfBoundsException exception) {
            log.info("There are no more posts in the feed. Let's go to the database.");
        }
        return result.stream().toList();
    }

    private void addPostIdToSubscriberFeed(Long postId, Long followerId) {
        RedisZSet<Long> postIds = redisFeedRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("No feed for user"))
                .getPostIds();
        checkMaxFeedSize(postIds);
        postIds.add(postId);
    }

    private void checkMaxFeedSize(RedisZSet<Long> postIds) {
        if (postIds.size() == maxFeedSize) {
            postIds.popFirst();
        }
    }
}
