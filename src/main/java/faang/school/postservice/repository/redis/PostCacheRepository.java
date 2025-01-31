package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostCache;
import org.springframework.data.repository.CrudRepository;

public interface PostCacheRepository extends CrudRepository<PostCache, Long> {

}
