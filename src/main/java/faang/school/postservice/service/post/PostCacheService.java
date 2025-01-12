package faang.school.postservice.service.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.redis.CachePost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.CachePostRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PostCacheService {
    private final CachePostRepository cachePostRepository;
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final RedissonClient redissonClient;
    @Value(value = "${spring.data.redis.post-cache.comments-in-post:3}")
    private int maxCommentsInPostCache;
    @Value("${spring.data.redis.redisson_client.name_version}")
    private String version;
    @Value("${spring.data.redis.redisson_client.key_for_version}")
    private String versionedKey;
    @Value("${spring.data.redis.redisson_client.start_num_for_version}")
    private int startNumForKey;
    @Value("${spring.data.redis.redisson_client.lock_cache_post}")
    private String lockCachePost;

    public List<CachePost> getPostCacheByIds(List<Long> postIds) {
        List<CachePost> listOfCachePost = new ArrayList<>();
        for (Long postId : postIds) {
            CachePost cachePost = getCachePost(postId);
            listOfCachePost.add(cachePost);
        }
        return listOfCachePost;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void incrementPostLikes(Long postId, Long likeId) {
        CachePost cachePost = getCachePost(postId);
        cachePost.getLikeIds().add(likeId);
        cachePost.incrementNumLikes();
        cachePostRepository.save(cachePost);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void addPostView(Long postId) {
        CachePost cachePost = getCachePost(postId);
        cachePost.incrementNumViews();
        cachePostRepository.save(cachePost);
    }

    public void addCommentToPostCache(Long postId, CommentDto commentDto) {
        CachePost cachePost = getCachePost(postId);
        CopyOnWriteArraySet<CommentDto> comments = cachePost.getComments();
        if (comments == null) {
            comments = new CopyOnWriteArraySet<>();
        }
        checkCapacity(comments);
        addCommentAndSave(commentDto, comments, cachePost);
    }

    private void addCommentAndSave(CommentDto commentDto, CopyOnWriteArraySet<CommentDto> comments, CachePost cachePost) {
        RLock lock = redissonClient.getLock(lockCachePost + cachePost.getId());
        try {
            if (lock.tryLock(2,1, TimeUnit.SECONDS)) {
                try {
                    RMap<String, Integer> versionMap = cachePost.getVersion();
                    Integer currentVersion = versionMap.getOrDefault(versionedKey, 1);
                    comments.add(commentDto);
                    Integer updatedVersion = versionMap.get(versionedKey);
                    if (!currentVersion.equals(updatedVersion)) {
                        throw new RuntimeException("The data was updated by another process.");
                    }
                    cachePost.setComments(comments);
                    cachePostRepository.save(cachePost);
                    versionMap.put(versionedKey, currentVersion + 1);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Could not acquire lock for post with id: " + cachePost.getId());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Blocking error", e);
        }
    }

    private void checkCapacity(CopyOnWriteArraySet<CommentDto> comments) {
        if (comments.size() >= maxCommentsInPostCache) {
            comments.stream().findFirst().ifPresent(comments::remove);
        }
    }

    private CachePost getCachePost(Long postId) {
        return cachePostRepository.save(cachePostRepository.findById(postId)
                .orElse(getCachePostFromDB(postId)));
    }

    private CachePost getCachePostFromDB(Long postId) {
        CachePost cachePost = mapper.toCachePost(postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("There is no such post with id:%d", postId))));
        RMap<String, Integer> versionMap = redissonClient.getMap(version);
        versionMap.put(versionedKey, startNumForKey);
        cachePost.setVersion(versionMap);
        return cachePost;
    }
}
