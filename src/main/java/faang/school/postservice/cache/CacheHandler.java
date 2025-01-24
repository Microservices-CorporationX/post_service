package faang.school.postservice.cache;

public interface CacheHandler<T> {
    void cache(T entity);
}
