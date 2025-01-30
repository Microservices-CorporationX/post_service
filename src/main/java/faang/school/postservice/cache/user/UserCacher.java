package faang.school.postservice.cache.user;

import faang.school.postservice.cache.CacheHandler;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.dto.user.UserCache;
import faang.school.postservice.repository.user.UserCacheRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCacher extends CacheHandler<PostCache> {

    private final Long cacheTtl;
    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Override
    @Retryable(retryFor = {Exception.class, RuntimeException.class, FeignException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    @Async("cacheExecutor")
    public void cache(PostCache postCache) {
        userContext.setUserId(postCache.getAuthorId());
        var user = userServiceClient.getUser(postCache.getAuthorId());
        var userCache = UserCache.builder()
                .id(user.getId())
                .username(user.getUsername())
                .ttl(cacheTtl)
                .build();
        cacheData(userCache, userCacheRepository::save);
    }
}
