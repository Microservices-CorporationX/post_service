package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);

    @Query("select c from Comment c join fetch c.post where c.post.id=:id")
    Optional<Comment> findByPostIdWithJoinFetch(long id);

    Optional<Comment> findByIdAndPostId(long id, long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findAllByPostIdOrderByCreatedAtDesc(long postId);
}
