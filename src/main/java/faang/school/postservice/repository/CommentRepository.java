package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query(nativeQuery = true, value = """
            SELECT c.* FROM comment c
            LEFT JOIN likes l ON c.id = l.comment_id
            WHERE c.post_id = :postId
            ORDER BY c.created_at DESC
            LIMIT :limit""")
    Optional<List<Comment>> findLastComments(@Param("postId") Long postId, @Param("limit") Long limit);
}
