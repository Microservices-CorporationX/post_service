package faang.school.postservice.service.redis;

import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCacheService {

    private final PostCacheRepository postCacheRepository;

    public void save(PostCache postCache) {
        log.info("Saving post with ID={} to cache...", postCache.getId());
        postCacheRepository.save(postCache);
        log.info("Post with ID={} saved to cache successfully", postCache.getId());
    }

    public void update(PostCache postCache) {
        log.info("Attempting to update post with ID={} in cache...", postCache.getId());
        postCacheRepository.findById(postCache.getId())
                .ifPresentOrElse(existing -> {
                    postCacheRepository.save(postCache);
                    log.info("Post with ID={} updated in cache", postCache.getId());
                }, () -> log.warn("Post with ID={} not found in cache to update", postCache.getId()));
    }

    public void delete(long postId) {
        log.info("Deleting post with ID={} from cache...", postId);
        postCacheRepository.deleteById(postId);
        log.info("Post with ID={} was deleted from cache", postId);
    }
}
