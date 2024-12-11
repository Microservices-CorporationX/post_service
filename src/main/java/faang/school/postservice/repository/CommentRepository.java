package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);

    @Query("select c from Comment c join fetch c.post where c.post.id=:id")
    Optional<Comment> findByPostIdWithJoinFetch(long id);

    Optional<Comment> findByIdAndPostId(long id, long postId);
}
