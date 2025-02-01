package faang.school.postservice.repository.redis;

import faang.school.postservice.model.cache.FeedCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisFeedRepository extends CrudRepository<FeedCache, Long> {

}
