package faang.school.postservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.kafka.producer.AuthorPostByHeatKafkaProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.model.dto.redis.cache.PostFields;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.kafka.AuthorPostKafkaEvent;
import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.FeedService;
import faang.school.postservice.service.RedisPostService;
import faang.school.postservice.service.RedisTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Value("${redis.feed.size}")
    private int newsFeedSize;

    @Value("${redis.feed.heater.time-range-days}")
    private int timeRangeDays;

    @Value("${redis.feed.heater.batch-size}")
    private int batchSize;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final RedisPostService redisPostService;
    private final PostMapper postMapper;
    private final RedisPostDtoMapper redisPostDtoMapper;
    private final AuthorPostByHeatKafkaProducer authorPostByHeatKafkaProducer;

    public FeedServiceImpl(
            @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            UserContext userContext,
            PostRepository postRepository,
            RedisPostService redisPostService,
            PostMapper postMapper,
            RedisPostDtoMapper redisPostDtoMapper,
            AuthorPostByHeatKafkaProducer authorPostByHeatKafkaProducer) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.userContext = userContext;
        this.postRepository = postRepository;
        this.redisPostService = redisPostService;
        this.postMapper = postMapper;
        this.redisPostDtoMapper = redisPostDtoMapper;
        this.authorPostByHeatKafkaProducer = authorPostByHeatKafkaProducer;
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

    //TODO пусть это выполняется ассинхронно, чтобы не держать порт открытым
    @Override
    public void startHeating() {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(timeRangeDays);
        LocalDateTime toDate = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, batchSize);
        Page<Post> page;

        do {
            page = postRepository.findPostsByDateRange(fromDate, toDate, pageable);
            List<Post> posts = page.getContent();
            List<RedisPostDto> redisPostDtoList = getRedisPostDtoList(posts);
            redisPostService.savePosts(redisPostDtoList);

            posts.forEach(post -> authorPostByHeatKafkaProducer.sendEvent(
                    new AuthorPostKafkaEvent(post.getId(), post.getAuthorId(), post.getPublishedAt())));

            pageable = pageable.next();
        } while (!page.isLast());
    }

    private List<RedisPostDto> getRedisPostDtoList(List<Post> posts) {
        return posts.stream()
                .map(postMapper::toPostDto)
                .map(redisPostDtoMapper::mapToRedisPostDto)
                .toList();
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
