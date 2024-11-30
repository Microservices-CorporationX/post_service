package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.CachePost;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;


public interface CachePostRepository extends KeyValueRepository<CachePost, Long> {
    Optional<CachePost> findById(Long id);
}
