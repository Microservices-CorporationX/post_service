package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.CacheUser;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface CacheUsersRepository extends KeyValueRepository<CacheUser, Long> {
}
