package faang.school.postservice.cache.post;

import faang.school.postservice.cache.CacheHandler;
import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.repository.post.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCacher implements CacheHandler<PostCache> {

    private final PostCacheRepository postCacheRepository;

    @Override
    @Async("cacheExecutor")
    public void cache(PostCache postCache) {
        var cache = postCacheRepository.save(postCache);
        log.info("Post with id {} has been cached, cache: {}", cache.getId(), cache);
    }
}
