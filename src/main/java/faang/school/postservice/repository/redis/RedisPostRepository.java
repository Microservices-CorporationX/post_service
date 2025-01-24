package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostRedis;
import org.springframework.data.repository.CrudRepository;

public interface RedisPostRepository extends CrudRepository<PostRedis, Long> {
}
