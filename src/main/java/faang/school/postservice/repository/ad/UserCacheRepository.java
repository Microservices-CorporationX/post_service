package faang.school.postservice.repository.ad;

import faang.school.postservice.model.redis.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends CrudRepository<UserCache, Long> {
}
