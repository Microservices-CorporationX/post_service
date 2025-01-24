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
public class PostCacher extends CacheHandler<PostCache> {

    private final Long cacheTtl;
    private final PostCacheRepository postCacheRepository;

    @Override
    @Async("cacheExecutor")
    public void cache(PostCache postCache) {
        cacheData(postCache, data -> {
            data.setTtl(cacheTtl);
            postCacheRepository.save(data);
        });
    }
}
