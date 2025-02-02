package faang.school.postservice.repository.cache_repository;

import faang.school.postservice.dto.news_feed_models.NewsFeedPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<NewsFeedPost, Long> {
}