package faang.school.postservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.dto.redis.cache.PostFields;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;
import faang.school.postservice.service.FeedService;
import faang.school.postservice.service.RedisTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class FeedServiceImpl implements FeedService, RedisTransactional {
    private static final String KEY_PREFIX = "newsfeed:user:";
    private static final String POST_KEY_PREFIX = "post:";
    private static final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Value("${redis.feed.size}")
    private int newsFeedSize;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserContext userContext;

    public FeedServiceImpl(
            @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            UserContext userContext) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.userContext = userContext;
    }

    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public void addPost(PostPublishedKafkaEvent event) {
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            String member = String.valueOf(event.getPostId());
            double score = toScore(event.getPublishedAt());

            for (Long followerId : event.getFollowerIds()) {
                String key = createKey(followerId);

                redisTemplate.opsForZSet().add(key, member, score);

                Long size = redisTemplate.opsForZSet().size(key);
                if (size != null && size > newsFeedSize) {
                    redisTemplate.opsForZSet().removeRange(key, 0, size - newsFeedSize - 1);
                }
            }
            return null;
        });
    }

    @Override
    public List<RedisPostDto> getNewsFeed(Long userId, int page, int pageSize) {
        if (!userId.equals(userContext.getUserId())) {
            throw new DataValidationException(
                    String.format("User with id = %d tried to feed of user with id = %d",
                            userContext.getUserId(), userId));
        }
        String key = createKey(userId);

        int start = page * pageSize;
        int end = start + pageSize - 1;

        Set<Object> postIds = redisTemplate.opsForZSet().reverseRange(key, start, end);
        if (postIds == null || postIds.isEmpty()) {
            return List.of();
        }

        List<RedisPostDto> posts = new ArrayList<>();
        for (Object postId : postIds) {
            String postKey = POST_KEY_PREFIX + postId;
            Map<Object, Object> postData = redisTemplate.opsForHash().entries(postKey);

            if (!postData.isEmpty()) {
                posts.add(convertMapToRedisPostDto(postData));
            }
        }

        return posts;
    }

    @Override
    public void startHeating() {

    }

    private RedisPostDto convertMapToRedisPostDto(Map<Object, Object> postData) {
        RedisPostDto postDto = new RedisPostDto();
        postDto.setPostId(Long.valueOf(postData.get(PostFields.POST_ID).toString()));
        postDto.setAuthorId(Long.valueOf(postData.get(PostFields.AUTHOR_ID).toString()));
        postDto.setContent((String) postData.get(PostFields.CONTENT));
        postDto.setCreatedAt(LocalDateTime.parse((String) postData.get(PostFields.CREATED_AT), formatter));
        postDto.setCommentCount(Integer.parseInt(postData.get(PostFields.COMMENT_COUNT).toString()));
        postDto.setLikeCount(Integer.parseInt(postData.get(PostFields.LIKE_COUNT).toString()));
        postDto.setRecentComments(getComments(postData));
        postDto.setViewCount(Integer.parseInt(postData.get(PostFields.VIEW_COUNT).toString()));
        return postDto;
    }

    private List<String> getComments(Map<Object, Object> postMap) {
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

    private String createKey(Long userId) {
        return KEY_PREFIX + userId;
    }

    private double toScore(LocalDateTime publishedAt) {
        return publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
