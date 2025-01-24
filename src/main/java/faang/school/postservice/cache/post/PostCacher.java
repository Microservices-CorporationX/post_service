package faang.school.postservice.cache.post;

import faang.school.postservice.cache.CacheHandler;
import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.repository.post.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCacher implements CacheHandler<PostCache> {

    @Value("${spring.data.redis.cache.time-to-live:2}")
    private long ttl;
    private final PostCacheRepository postCacheRepository;

    @Override
    @Async("cacheExecutor")
    public void cache(PostCache postCache) {
        postCache.setTtl(ttl);
        var cache = postCacheRepository.save(postCache);
        log.info("Post with id {} has been cached, cache: {}", cache.getId(), cache);
    }
}
