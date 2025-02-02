package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.FeedCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCacheRepository extends CrudRepository<FeedCache, Long> {

}
