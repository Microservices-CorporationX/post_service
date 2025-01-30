package faang.school.postservice.repository.cache;

import faang.school.postservice.model.cache.Feed;
import org.springframework.data.repository.ListCrudRepository;

public interface RedisFeedRepository extends ListCrudRepository<Feed, Long> {
}
