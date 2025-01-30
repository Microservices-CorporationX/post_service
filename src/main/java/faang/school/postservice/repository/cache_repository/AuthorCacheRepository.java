package faang.school.postservice.repository.cache_repository;

import faang.school.postservice.cache_entities.AuthorCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRepository extends CrudRepository<AuthorCache, Long> {
}