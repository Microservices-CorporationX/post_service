package faang.school.postservice.repository.cache;

import faang.school.postservice.model.cache.PostCache;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends ListCrudRepository<PostCache, Long> {
}
