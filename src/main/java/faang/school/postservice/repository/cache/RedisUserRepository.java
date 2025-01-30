package faang.school.postservice.repository.cache;

import faang.school.postservice.model.cache.UserCache;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends ListCrudRepository<UserCache, Long> {

}
