package faang.school.postservice.cache.post;

import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.repository.post.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCacherImpl implements PostCacher {

    private final PostCacheRepository postCacheRepository;

    @Override
    @Async("cacheExecutor")
    public void cachePost(PostCache postCache) {
        var cache = postCacheRepository.save(postCache);
        log.info("all cache: {}", postCacheRepository.findById(postCache.getId().toString()));
        log.info("Post with id {} has been cached, cache: {}", postCache.getId(), cache);
    }
}
