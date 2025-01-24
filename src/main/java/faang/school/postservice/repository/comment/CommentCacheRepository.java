package faang.school.postservice.repository.comment;

import faang.school.postservice.dto.comment.CommentCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCacheRepository extends CrudRepository<CommentCache, String> {
}
