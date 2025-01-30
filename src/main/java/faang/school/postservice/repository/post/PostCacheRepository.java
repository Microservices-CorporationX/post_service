package faang.school.postservice.repository.post;

import faang.school.postservice.dto.post.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCache, String> {
}
