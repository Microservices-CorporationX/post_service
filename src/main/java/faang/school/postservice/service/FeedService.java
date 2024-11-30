package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.KafkaFeedHeaterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UsersCacheMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.redis.CacheUser;
import faang.school.postservice.publisher.KafkaFeedHeaterProducer;
import faang.school.postservice.repository.redis.CacheRedisRepository;
import faang.school.postservice.repository.redis.CachePostRepository;
import faang.school.postservice.repository.redis.CacheUsersRepository;
import faang.school.postservice.service.post.PostCacheService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final PostCacheService postCacheService;
    private final PostService postService;
    private final CachePostRepository cachePostRepository;
    private final CacheUsersRepository cacheUsersRepository;
    private final KafkaFeedHeaterProducer kafkaFeedHeaterProducer;
    private final UserCacheService userCacheService;
    private final UsersCacheMapper usersCacheMapper;
    private final UserServiceClient userClient;
    private final PostMapper mapper;
    private final CacheRedisRepository cacheFeedRepository;

    @Value("${spring.data.redis.feed-cache.batch_size:20}")
    private int batchSize;
    @Value("${spring.data.redis.feed-cache.max_feed_size:500}")
    private int maxFeedSize;

    public List<PostDto> getFeedByUserId(Long postId, long userId) {
        List<Long> followerPostIds = getFollowerPostIds(userId, postId);
        List<PostDto> listPostDto;
        if (followerPostIds.isEmpty()) {
            CacheUser cacheUser = getCacheUser(userId);
            Long startPostId = postId == null ? 1L : postId;
            listPostDto = postService.getPostsByAuthorIds(cacheUser.getFolloweesIds(), startPostId, batchSize);
            saveToPostCache(listPostDto);
            cacheFeedRepository.saveAll(cacheUser.getId(), listPostDto);
        } else {
            listPostDto = postCacheService.getPostCacheByIds(followerPostIds).stream()
                    .map(mapper::toDto)
                    .toList();
        }
        return listPostDto;
    }

    public void addPostIdToAuthorSubscribers(Long postId, List<Long> subscriberIds) {
        subscriberIds.forEach(subscriberId -> addPostIdToSubscriberFeed(postId, subscriberId));
    }

    public void sendHeatEvents() {
        List<UserDto> allUsers = userClient.getAllUsers();
        cacheUsersRepository.saveAll(allUsers.stream()
                .map(usersCacheMapper::toCacheUser)
                .toList());
        List<Long> userIds = allUsers.stream().map(UserDto::getId).toList();
        List<List<Long>> separatedUserIds = splitList(userIds);
        separatedUserIds.stream()
                .map(KafkaFeedHeaterDto::new)
                .forEach(kafkaFeedHeaterProducer::publish);
    }

    private void saveToPostCache(List<PostDto> listPostDto) {
        cachePostRepository.saveAll(listPostDto.stream()
                .map(mapper::toEntity)
                .map(mapper::toCachePost)
                .toList());
    }

    private List<Long> getFollowerPostIds(Long userId, Long postId) {
        Set<Long> postIds = cacheFeedRepository.find(userId);
        if (postIds.isEmpty()) {
            return emptyList();
        }
        if (postId == null) {
            return getFeedInRange(userId, 0, batchSize - 1);
        } else {
            long rank = cacheFeedRepository.getRank(userId, postId);
            return getFeedInRange(userId, rank + 1, rank + batchSize);
        }

    }

    private List<Long> getFeedInRange(Long userId, long startPostId, long endPostId) {
        Set<Object> result = new HashSet<>();
        try {
            result = cacheFeedRepository.getRange(userId, startPostId, endPostId);
        } catch (ArrayIndexOutOfBoundsException exception) {
            log.info("There are no more posts in the feed. Let's go to the database.");
        }
        return result.stream().map(obj -> Long.valueOf(String.valueOf(obj))).toList();
    }

    private void addPostIdToSubscriberFeed(Long postId, Long followerId) {
        Set<Long> postIds = cacheFeedRepository.find(followerId);
        checkMaxFeedSize(postIds);
        cacheFeedRepository.add(followerId, postId);
    }

    private void checkMaxFeedSize(Set<Long> postIds) {
        if (postIds.size() == maxFeedSize) {
            postIds.stream().findFirst().ifPresent(postIds::remove);
        }
    }

    private CacheUser getCacheUser(Long userId) {
        Optional<CacheUser> cacheUser = userCacheService.getCacheUser(userId);
        return cacheUser.orElseGet(() -> userCacheService.saveCacheUser(userId));
    }

    private List<List<Long>> splitList(List<Long> userIds) {
        return IntStream
                .range(0, (userIds.size() + batchSize - 1) / batchSize)
                .mapToObj(num -> userIds
                        .subList(num * batchSize, Math.min(batchSize * (num + 1), userIds.size())))
                .toList();
    }
}
