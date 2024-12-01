package faang.school.postservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.UserWithoutFollowersDto;
import faang.school.postservice.model.dto.redis.cache.PostFields;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.dto.redis.cache.RedisUserDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.kafka.CommentSentKafkaEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.RedisPostService;
import faang.school.postservice.service.RedisTransactional;
import faang.school.postservice.service.RedisUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisPostServiceImpl implements RedisPostService, RedisTransactional {
    private static final String USER_KEY_PREFIX = "user:";
    private static final String POST_KEY_PREFIX = "post:";
    private static final String LIKE_KEY_PREFIX = "like:";
    private static final String POST_VIEW_KEY_PREFIX = "postView:";
    private static final String COMMENT_KEY_PREFIX = "comment:";
    private static final String VIEW_DATE_TIME = "viewDateTime:";
    private static final int REFRESH_TIME_IN_HOURS = 3;
    private static final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Value("${redis.feed.ttl.comment:86400}")
    private long commentTtlInSeconds;

    @Value("${redis.feed.ttl.post:86400}")
    private long postTtlInSeconds;

    @Value("${redis.feed.ttl.post-view:86400}")
    private long postViewTtlInSeconds;

    @Value("${redis.feed.comment.max-size:3}")
    private int maxRecentComments;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;
    private final RedisPostDtoMapper redisPostDtoMapper;
    private final PostMapper postMapper;
    private final RedisUserService redisUserService;
    private final UserServiceClient userServiceClient;

    public RedisPostServiceImpl(
            @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            PostRepository postRepository,
            RedisPostDtoMapper redisPostDtoMapper,
            PostMapper postMapper,
            RedisUserService redisUserService,
            UserServiceClient userServiceClient) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.postRepository = postRepository;
        this.redisPostDtoMapper = redisPostDtoMapper;
        this.postMapper = postMapper;
        this.redisUserService = redisUserService;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public void savePostIfNotExists(RedisPostDto postDto) {
        String key = createPostKey(postDto.getPostId());
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            log.info("Post with ID {} already exists in Redis, skipping...", postDto.getPostId());
            return;
        }
        savePost(postDto);
    }

    @Override
    public RedisPostDto getPost(Long postId) {
        String key = createPostKey(postId);
        Map<String, Object> postMap = fetchAndCachePostIfAbsent(postId, key);
        return convertMapToPostDto(postMap);
    }

    @Override
    public void addComment(CommentSentKafkaEvent event) {
        RedisUserDto user = redisUserService.getUser(event.getCommentAuthorId());
        if (user == null || user.getUpdatedAt().isBefore(LocalDateTime.now().minusHours(REFRESH_TIME_IN_HOURS))) {
            UserWithoutFollowersDto commentAuthor = userServiceClient.getUserWithoutFollowers(event.getCommentAuthorId());
            user = new RedisUserDto(
                    commentAuthor.getUserId(),
                    commentAuthor.getUsername(),
                    commentAuthor.getFileId(),
                    commentAuthor.getSmallFileId(),
                    null,
                    LocalDateTime.now());
            redisUserService.saveUser(user);
        }

        addComment(event.getPostId(), event.getCommentId(), event.getCommentContent());
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void incrementLikesWithTransaction(Long postId, Long likeId) {
        String likeKey = createLikeKey(likeId);
        String postKey = createPostKey(postId);
        executeRedisTransaction(() -> {
            boolean isAlreadyProcessed = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(
                    likeKey,
                    "processed",
                    Duration.ofSeconds(postViewTtlInSeconds)));

            if (!isAlreadyProcessed) {
                log.debug("Like event for post {} is already processed", postId);
                return;
            }
            fetchAndCachePostIfAbsent(postId, postKey);
            redisTemplate.opsForHash().increment(postKey, PostFields.LIKE_COUNT, 1);
        });
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void savePost(RedisPostDto postDto) {
        String key = createPostKey(postDto.getPostId());
        executeRedisTransaction(() -> {
            Map<String, Object> postMap = convertPostDtoToMap(postDto);
            postMap.forEach((field, value) -> redisTemplate.opsForHash().put(key, field, value));
            updatePostTtl(key);
        });
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void incrementPostViewsWithTransaction(Long postId, Long viewerId, String viewDateTime) {
        String postViewKey = createPostViewKey(postId, viewerId, viewDateTime);
        String postKey = createPostKey(postId);
        executeRedisTransaction(() -> {
            boolean isAlreadyProcessed = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(
                    postViewKey,
                    "processed",
                    Duration.ofSeconds(postViewTtlInSeconds)));

            if (!isAlreadyProcessed) {
                log.debug("Post view event for post {} is already processed", postId);
                return;
            }
            fetchAndCachePostIfAbsent(postId, postKey);
            redisTemplate.opsForHash().increment(postKey, PostFields.VIEW_COUNT, 1);
        });
    }

    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    protected void addComment(Long postId, Long commentId, String commentContent) {
        String commentKey = createCommentKey(commentId);
        String postKey = createPostKey(postId);
        executeRedisTransaction(() -> {
            boolean isAlreadyProcessed = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(
                    commentKey,
                    "processed",
                    Duration.ofSeconds(commentTtlInSeconds)));

            if (!isAlreadyProcessed) {
                log.debug("Comment event {} for post {} is already processed", commentId, postId);
                return;
            }

            Map<String, Object> postMap = fetchAndCachePostIfAbsent(postId, postKey);
            List<String> recentComments = getComments(postMap);
            recentComments.add(0, commentContent);
            if (recentComments.size() > maxRecentComments) {
                recentComments = recentComments.subList(0, maxRecentComments);
            }
            int commentCount = Integer.parseInt(postMap.getOrDefault(PostFields.COMMENT_COUNT, "0").toString()) + 1;

            redisTemplate.opsForHash().put(postKey, PostFields.RECENT_COMMENTS, serializeComments(recentComments));
            redisTemplate.opsForHash().put(postKey, PostFields.COMMENT_COUNT, String.valueOf(commentCount));
        });
    }

    private void updatePostTtl(String key) {
        redisTemplate.expire(key, postTtlInSeconds, TimeUnit.SECONDS);
    }

    private RedisPostDto fetchPostFromDatabase(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with id = %d not found in DB", postId)));
        PostDto postDto = postMapper.toPostDto(post);
        return redisPostDtoMapper.mapToRedisPostDto(postDto);
    }

    private RedisPostDto convertMapToPostDto(Map<String, Object> postMap) {
        RedisPostDto postDto = new RedisPostDto();
        postDto.setPostId(Long.valueOf(postMap.get(PostFields.POST_ID).toString()));
        postDto.setAuthorId(Long.valueOf(postMap.get(PostFields.AUTHOR_ID).toString()));
        postDto.setContent((String) postMap.get(PostFields.CONTENT));
        postDto.setCreatedAt(LocalDateTime.parse((String) postMap.get(PostFields.CREATED_AT), formatter));
        postDto.setCommentCount(Integer.parseInt(postMap.get(PostFields.COMMENT_COUNT).toString()));
        postDto.setLikeCount(Integer.parseInt(postMap.get(PostFields.LIKE_COUNT).toString()));
        postDto.setRecentComments(getComments(postMap));
        postDto.setViewCount(Integer.parseInt(postMap.get(PostFields.VIEW_COUNT).toString()));
        return postDto;
    }

    private Map<String, Object> convertPostDtoToMap(RedisPostDto postDto) {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put(PostFields.POST_ID, postDto.getPostId().toString());
        postMap.put(PostFields.AUTHOR_ID, postDto.getAuthorId().toString());
        postMap.put(PostFields.CONTENT, postDto.getContent());
        postMap.put(PostFields.CREATED_AT, postDto.getCreatedAt().format(formatter));
        postMap.put(PostFields.COMMENT_COUNT, String.valueOf(postDto.getCommentCount()));
        postMap.put(PostFields.LIKE_COUNT, String.valueOf(postDto.getLikeCount()));
        postMap.put(PostFields.RECENT_COMMENTS, serializeComments(postDto.getRecentComments()));
        postMap.put(PostFields.VIEW_COUNT, String.valueOf(postDto.getViewCount()));
        return postMap;
    }

    private List<String> getComments(Map<String, Object> postMap) {
        Object commentsObj = postMap.get(PostFields.RECENT_COMMENTS);
        if (commentsObj == null) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(commentsObj.toString(), new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize comments", e);
            return new ArrayList<>();
        }
    }

    private String serializeComments(List<String> comments) {
        try {
            return objectMapper.writeValueAsString(comments);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize comments, returning empty list", e);
            return "[]";
        }
    }

    private String createPostKey(Long postId) {
        return POST_KEY_PREFIX + postId;
    }

    private String createLikeKey(Long likeId) {
        return LIKE_KEY_PREFIX + likeId;
    }

    private String createPostViewKey(Long postId, Long viewerId, String viewDateTime) {
        return String.format("%s%s%d:%s%d:%s%s",
                POST_VIEW_KEY_PREFIX, POST_KEY_PREFIX, postId,
                USER_KEY_PREFIX, viewerId,
                VIEW_DATE_TIME, viewDateTime);
    }

    private String createCommentKey(Long commentId) {
        return COMMENT_KEY_PREFIX + commentId;
    }

    private Map<String, Object> fetchAndCachePostIfAbsent(Long postId, String key) {
        Map<Object, Object> postMap = redisTemplate.opsForHash().entries(key);
        if (postMap.isEmpty()) {
            log.warn("Post with ID {} not found in Redis, fetching from database", postId);
            RedisPostDto postFromDb = fetchPostFromDatabase(postId);
            //TODO savePost может вернуть postMap и мы можем обойтись без convertPostDtoToMap
            savePost(postFromDb);
            return convertPostDtoToMap(postFromDb);
        }
        return postMap.entrySet().stream()
                .collect(HashMap::new, (m, e) -> m.put(e.getKey().toString(), e.getValue()), Map::putAll);
    }
}