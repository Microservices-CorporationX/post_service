package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentForNewsFeedDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostForNewsFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserForNewsFeedDto;
import faang.school.postservice.dto.user.UserForNewsFeedResponseDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.message.event.CommentEvent;
import faang.school.postservice.message.event.LikeEvent;
import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.message.event.PostViewEvent;
import faang.school.postservice.message.producer.KafkaCommentPublisher;
import faang.school.postservice.message.producer.KafkaLikePublisher;
import faang.school.postservice.message.producer.KafkaPostPublisher;
import faang.school.postservice.message.producer.KafkaPostViewPublisher;
import faang.school.postservice.model.cache.CommentCache;
import faang.school.postservice.model.cache.Feed;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.cache.RedisFeedRepository;
import faang.school.postservice.repository.cache.RedisPostRepository;
import faang.school.postservice.repository.cache.RedisUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {

    private final PostService postService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final RedisPostRepository redisPostRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final UserServiceClient userServiceClient;
    private final UserMapper userMapper;
    private final LikeMapper likeMapper;
    private final RedisUserRepository redisUserRepository;
    private final KafkaPostPublisher kafkaPostPublisher;
    private final KafkaCommentPublisher kafkaCommentPublisher;
    private final KafkaLikePublisher kafkaLikePublisher;
    private final KafkaPostViewPublisher kafkaPostViewPublisher;
    private final ExpirableLockRegistry redisLockRegistry;
    private final TaskExecutor threadPool;

    @Value("${spring.kafka.topic.posts.users-per-event}")
    private int usersPerEvent;

    @Value("${spring.data.redis.entity.feed.max-feed-size}")
    private int maxCacheFeedSize;

    @Value("${feed.posts-per-page}")
    private int postsPerPage;

    @Value("${feed.comments-per-post}")
    private int commentsPerPost;

    @Value("${spring.data.redis.entity.feed.name}")
    private String feedCacheName;

    @Value("${spring.data.redis.entity.post.name}")
    private String postCacheName;

    public List<PostForNewsFeedDto> getFeed(long userId, Long lastPostId) {
        log.info("Request to get feed for user under id {}. Last post id: {}", userId, lastPostId);
        Optional<Feed> feedOptional = redisFeedRepository.findById(userId);
        if (feedOptional.isEmpty()) {
            log.info("User under id {} has no feed in cache. Returning empty feed", userId);
            return new ArrayList<>();
        }

        Feed feed = feedOptional.get();
        List<Long> postIds = feed.getLastNPosts(postsPerPage, lastPostId);
        return getFeedByPostIds(postIds, userId, lastPostId);
    }

    @Async("threadPool")
    public void addPostToCacheAsync(PostDto postDto) {
        threadPool.execute(() -> savePostToCache(postDto));
        UserForNewsFeedDto user = userServiceClient.getUserForNewsFeed(postDto.authorId());
        threadPool.execute(() -> saveUserToCache(user));
        List<PostEvent> postEvents = getPostEventBatches(postDto.id(), user.followerIds());
        kafkaPostPublisher.publishPostEvents(postEvents);
    }

    @Async("threadPool")
    public void savePostToCache(PostDto postDto) {
        log.info("Trying to save post {} to cache", postDto);
        PostCache postCache = postMapper.toCachedPost(postDto);
        redisPostRepository.save(postCache);
    }

    public void addPostToFollowersFeedInCache(PostEvent postEvent) {
        log.info("Trying to add post to subscribers feed {}", postEvent);
        for (long followerId : postEvent.followerIds()) {
            log.info("Trying to add post to user under id {}", followerId);
            Lock lock = redisLockRegistry.obtain(feedCacheName + followerId);
            lock.lock();
            Feed feed = getFeedElseCreate(followerId);
            feed.addPostToFeed(postEvent.postId(), maxCacheFeedSize);
            redisFeedRepository.save(feed);
            lock.unlock();
        }
    }

    @Async("threadPool")
    public void sendCommentEventAsync(CommentDto commentDto) {
        log.info("Trying to send comment event with for post under id {}", commentDto.postId());
        CommentEvent commentEvent = commentMapper.toCommentEvent(commentDto);
        kafkaCommentPublisher.publishCommentEvent(commentEvent);
    }

    public void addCommentToPostCache(CommentEvent commentEvent) {
        log.info("Trying to add comment {} to post under id {}", commentEvent.content(), commentEvent.postId());
        Consumer<PostCache> consumer = (post) -> {
            CommentCache commentCache = commentMapper.toCommentCache(commentEvent);
            post.addComment(commentCache, commentsPerPost);
        };

        processKafkaEventOnPost(commentEvent.postId(), consumer);
    }

    @Async("threadPool")
    public void sendLikeEventAsync(LikePostDto likePostDto) {
        log.info("Trying to send like post event for post under id {}", likePostDto.postId());
        LikeEvent likeEvent = likeMapper.toLikeEvent(likePostDto);
        kafkaLikePublisher.publishLikeEvent(likeEvent);
    }

    public void incrementLikeCount(LikeEvent likeEvent) {
        log.info("Trying to increment like count for post under id {}", likeEvent.postId());
        Consumer<PostCache> consumer = PostCache::incrementLikeCount;
        processKafkaEventOnPost(likeEvent.postId(), consumer);
    }

    @Async("threadPool")
    public void sendPostViewEventAsync(long postId) {
        log.info("Trying to send post view event for post under id {}", postId);
        PostViewEvent postViewEvent = new PostViewEvent(postId);
        kafkaPostViewPublisher.publishPostViewEvent(postViewEvent);
    }

    public void incrementPostViewCount(PostViewEvent postViewEvent) {
        log.info("Trying to increase post view count of post under id {}", postViewEvent.postId());
        Consumer<PostCache> consumer = PostCache::incrementViewCount;
        processKafkaEventOnPost(postViewEvent.postId(), consumer);
    }

    @Async("threadPool")
    public void saveUserToCacheById(long userId) {
        log.info("Trying to save user under id {} to cache", userId);
        UserForNewsFeedDto user = userServiceClient.getUserForNewsFeed(userId);
        saveUserToCache(user);
    }

    @Async("threadPool")
    public void saveUserToCache(UserForNewsFeedDto user) {
        log.info("Trying to save user {} to cache", user);
        UserCache userCache = userMapper.toUserCache(user);
        redisUserRepository.save(userCache);
    }

    private List<PostForNewsFeedDto> getFeedByPostIds(List<Long> postIds, long userId, Long lastPostId) {
        log.info("Trying to get feed for user under id {} with the following post ids: {}", userId, postIds);
        if (postIds.isEmpty()) {
            log.info("Post ids list is empty for user under id {}. Trying to get posts from database", userId);
            List<PostForNewsFeedDto> posts = postService.getLastNPostsByUserIdStartingFromPost(userId, postsPerPage, lastPostId);
            posts.forEach(post -> mapToFullPostForNewsFeedDto(post, userId, commentsPerPost));
            return posts;
        }

        return postIds.stream()
                .map(postId -> mapPostIdToPostForNewsFeedDto(postId, userId))
                .toList();
    }

    private List<PostEvent> getPostEventBatches(long postId, List<Long> followerIds) {
        log.info("Trying to get batches of post events for postId {} and followersIds {}", postId, followerIds);
        List<List<Long>> batches = ListUtils.partition(followerIds, usersPerEvent);
        return batches.stream()
                .map(batch -> new PostEvent(postId, batch))
                .toList();
    }

    private Feed getFeedElseCreate(long followerId) {
        log.info("Trying to get feed of user under id {} from cache", followerId);
        Optional<Feed> feedOptional = redisFeedRepository.findById(followerId);
        return feedOptional.orElse(new Feed(followerId));
    }

    private void processKafkaEventOnPost(long postId, Consumer<PostCache> consumer) {
        log.info("Trying to process kafka event for post under id {}", postId);
        String lockKey = postCacheName + postId;
        Lock lock = redisLockRegistry.obtain(lockKey);
        lock.lock();

        try {
            PostCache post = getPostCacheById(postId);
            consumer.accept(post);
            redisPostRepository.save(post);
        } finally {
            lock.unlock();
        }
    }

    private PostCache getPostCacheById(long postId) {
        log.info("Trying to get post from cache by id {}", postId);
        return redisPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Post under id %d is not in cache", postId)
                ));
    }

    private void mapToFullPostForNewsFeedDto(PostForNewsFeedDto post, long userId, int numComments) {
        post.setUser(mapToFullUserForNewsFeedResponseDto(userId));
        post.getComments().forEach(comment -> comment.setUser(mapToFullUserForNewsFeedResponseDto(comment.getUser().getId())));
        post.setComments(
                post.getComments().stream()
                        .sorted(Comparator.comparingLong(CommentForNewsFeedDto::getId).reversed())
                        .limit(numComments)
                        .toList()
        );
    }

    private PostForNewsFeedDto mapPostIdToPostForNewsFeedDto(long postId, long userId) {
        PostForNewsFeedDto post = getPostForNewsFeedDto(postId, userId, commentsPerPost);
        post.setUser(mapToFullUserForNewsFeedResponseDto(post.getUser().getId()));
        post.getComments().forEach(comment -> comment.setUser(mapToFullUserForNewsFeedResponseDto(comment.getUser().getId())));
        return post;
    }

    private PostForNewsFeedDto getPostForNewsFeedDto(long postId, long userId, int commentsPerPost) {
        Optional<PostCache> postOptional = redisPostRepository.findById(postId);
        if (postOptional.isEmpty()) {
            PostForNewsFeedDto post = postService.getPostForNewsFeed(postId, userId);
            mapToFullPostForNewsFeedDto(post, userId, commentsPerPost);
            return post;
        }
        return postMapper.toPostForNewsFeedDto(postOptional.get());
    }

    private UserForNewsFeedResponseDto mapToFullUserForNewsFeedResponseDto(long userId) {
        Optional<UserCache> userOptional = redisUserRepository.findById(userId);
        if (userOptional.isEmpty()) {
            UserDto userDto = userServiceClient.getUser(userId);
            return userMapper.toUserForNewsFeedResponseDto(userDto);
        }
        return userMapper.toUserForNewsFeedResponseDto(userOptional.get());
    }
}