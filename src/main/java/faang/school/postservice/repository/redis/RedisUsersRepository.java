package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.RedisUser;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface RedisUsersRepository extends KeyValueRepository<RedisUser, Long> {
}
