package faang.school.postservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheFacade<T> {

    private final List<CacheHandler<T>> cacheHandlers;

    public void cacheWithDetails(T entity) {
        for (CacheHandler<T> handler : cacheHandlers) {
            handler.cache(entity);
        }
    }
}
