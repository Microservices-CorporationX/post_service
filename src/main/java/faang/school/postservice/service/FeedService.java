package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.KafkaFeedHeaterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UsersCacheMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.redis.CacheUser;
import faang.school.postservice.publisher.KafkaFeedHeaterProducer;
import faang.school.postservice.repository.redis.CacheFeedRepository;
import faang.school.postservice.repository.redis.CachePostRepository;
import faang.school.postservice.repository.redis.CacheUsersRepository;
import faang.school.postservice.service.post.PostCacheService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private final CacheFeedRepository cacheFeedRepository;
    private final RedissonClient redissonClient;

    @Value("${spring.data.redis.feed-cache.batch_size:20}")
    private int batchSize;
    @Value("${spring.data.redis.feed-cache.max_feed_size:500}")
    private int maxFeedSize;
    @Value("${spring.data.redis.feed-cache.number_of_users_on_page:3}")
    private int numberOfUsersOnPage;
    @Value("${spring.data.redis.redisson_client.name_version}")
    private String version;
    @Value("${spring.data.redis.redisson_client.key_for_version}")
    private String versionedKey;
    @Value("${spring.data.redis.redisson_client.start_num_for_version}")
    private int startNumForKey;

    public List<PostDto> getFeedByUserId(long userId, Long postId) {
        List<Long> followerPostIds = getFollowerPostIds(userId, postId);
        List<PostDto> listPostDto;
        if (followerPostIds.isEmpty()) {
            listPostDto = getPostDtos(userId, postId);
        } else {
            listPostDto = getPostDtosIfFollowerPostIdsNotEmpty(followerPostIds);
        }
        return listPostDto;
    }

    public List<PostDto> getFeedByUserId(long userId) {
        List<Long> followerPostIds = getFollowerPostIds(userId);
        List<PostDto> listPostDto;
        if (followerPostIds.isEmpty()) {
            listPostDto = getPostDtos(userId, 1L);
        } else {
            listPostDto = getPostDtosIfFollowerPostIdsNotEmpty(followerPostIds);
        }
        return listPostDto;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addPostIdToAuthorSubscribers(Long postId, List<Long> subscriberIds) {
        subscriberIds.forEach(subscriberId -> addPostIdToSubscriberFeed(postId, subscriberId));
    }

    public void sendHeatEvents() {
        Long countUsers = userClient.getCountUser();
        for (int i = 0; i < countUsers / numberOfUsersOnPage + 1; i++) {
            Page<UserDto> users = userClient.getUsers(i, numberOfUsersOnPage);
            cacheUsersRepository.saveAll(users.stream()
                    .map(usersCacheMapper::toCacheUser)
                    .toList());
            List<Long> userIds = users.stream().map(UserDto::getId).toList();
            List<List<Long>> separatedUserIds = ListUtils.partition(userIds, batchSize);
            separatedUserIds.stream()
                    .map(KafkaFeedHeaterDto::new)
                    .forEach(kafkaFeedHeaterProducer::publish);
        }
    }

    private List<PostDto> getPostDtos(long userId, long startPostId) {
        List<PostDto> listPostDto;
        CacheUser cacheUser = getCacheUser(userId);
        listPostDto = postService.getPostsByAuthorIds(cacheUser.getFolloweesIds(), startPostId, batchSize);
        saveToPostCache(listPostDto);
        cacheFeedRepository.saveAll(cacheUser.getId(), listPostDto);
        return listPostDto;
    }

    private List<PostDto> getPostDtosIfFollowerPostIdsNotEmpty(List<Long> followerPostIds) {
        return postCacheService.getPostCacheByIds(followerPostIds).stream()
                .map(mapper::toDto)
                .toList();
    }

    private void saveToPostCache(List<PostDto> listPostDto) {
        cachePostRepository.saveAll(listPostDto.stream()
                .map(mapper::toEntity)
                .map(mapper::toCachePost)
                .peek(cachePost -> {
                    RMap<String, Integer> versionMap = redissonClient.getMap(version);
                    versionMap.put(versionedKey, startNumForKey);
                    cachePost.setVersion(versionMap);
                })
                .toList());

    }

    private List<Long> getFollowerPostIds(Long userId, Long postId) {
        Set<Long> postIds = cacheFeedRepository.find(userId);
        if (postIds.isEmpty()) {
            return emptyList();
        }
        else {
            long rank = cacheFeedRepository.getRank(userId, postId);
            return getFeedInRange(userId, rank + 1, rank + batchSize);
        }
    }

    private List<Long> getFollowerPostIds(Long userId) {
        Set<Long> postIds = cacheFeedRepository.find(userId);
        if (postIds.isEmpty()) {
            return emptyList();
        }
        return getFeedInRange(userId, 0, batchSize - 1);
    }

    private List<Long> getFeedInRange(Long userId, long startPostId, long endPostId) {
        Set<Object> result = cacheFeedRepository.getRange(userId, startPostId, endPostId);
        return result.stream().map(String::valueOf).map(Long::valueOf).toList();
    }

    private void addPostIdToSubscriberFeed(Long postId, Long followerId) {
        Set<Long> postIds = cacheFeedRepository.find(followerId);
        checkMaxFeedSize(postIds);
        cacheFeedRepository.add(followerId, postId);
    }

    private void checkMaxFeedSize(Set<Long> postIds) {
        if (postIds.size() >= maxFeedSize) {
            postIds.stream().findFirst().ifPresent(postIds::remove);
        }
    }

    private CacheUser getCacheUser(Long userId) {
        Optional<CacheUser> cacheUser = userCacheService.getCacheUser(userId);
        return cacheUser.orElseGet(() -> userCacheService.saveCacheUser(userId));
    }
}
