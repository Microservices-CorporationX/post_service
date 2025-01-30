package faang.school.postservice.repository.cache_repository;

import faang.school.postservice.cache_entities.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCache, Long> {
}