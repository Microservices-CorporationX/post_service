package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query(nativeQuery = true,
            value = """
                    SELECT * FROM comment
                             WHERE post_id = :postId
                             ORDER BY created_at DESC
                             LIMIT :limit
                    """)
    List<Comment> findLatestByPostId(@Param("postId") long postId, @Param("limit") int limit);
}
