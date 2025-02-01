package faang.school.postservice.repository.cache_repository;

import faang.school.postservice.dto.news_feed_models.NewsFeedAuthor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRepository extends CrudRepository<NewsFeedAuthor, Long> {
}