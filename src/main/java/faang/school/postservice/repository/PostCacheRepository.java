package faang.school.postservice.repository;

import faang.school.postservice.model.redis.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCache, Long> {
}
