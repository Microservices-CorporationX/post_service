package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.RedisFeed;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;

public interface RedisFeedRepository extends KeyValueRepository<RedisFeed, Long> {
    Optional<RedisFeed> findByFollowerIdBetween(Long followerId, Long startId, Long endId);
}
