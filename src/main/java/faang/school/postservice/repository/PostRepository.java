package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.published = false")
    List<Post> findByPublishedFalse();

    default Post getPostById(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));
    }

    List<Post> findByVerifiedIsNull();

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.published = true
              AND p.deleted = false
              AND p.authorId IN :userSubscriptions
              AND p.publishedAt < (
                SELECT p2.publishedAt
                FROM Post p2
                WHERE p2.id = :postPointerId
              )
            ORDER BY p.publishedAt DESC
            LIMIT :batchSize
            """)
    List<Post> getFeedForUser(List<Long> userSubscriptions, Long postPointerId, int batchSize);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.published = true
              AND p.deleted = false
              AND p.authorId IN :userSubscriptions
            ORDER BY p.publishedAt  DESC
            LIMIT :batchSize
            """)
    List<Post> getFeedForUser(List<Long> userSubscriptions, int batchSize);
}