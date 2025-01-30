package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    List<Post> findByVerifiedDateIsNull();

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.id = :postId")
    Post findByPostIdWithLikes(long postId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes l WHERE p.authorId IN :followerIds AND p.id < :lastPostId ORDER BY p.id DESC")
    List<Post> findLastNPostsByUserIdStartingFromPostWithLikes(List<Long> followerIds, long lastPostId, Pageable pageable);
}
