package faang.school.postservice.repository.user;

import faang.school.postservice.dto.user.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends CrudRepository<UserCache, String> {
}