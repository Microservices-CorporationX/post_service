package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import feign.Param;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

    void deleteByPostIdAndUserId(long postId, long userId);

    void deleteByCommentIdAndUserId(long commentId, long userId);

    List<Like> findByPostId(long postId);

    List<Like> findByCommentId(long commentId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    // Запрос для получения userId по postId
    @Query("SELECT l.userId FROM Like l WHERE l.post.id = :postId")
    List<Long> findUserIdsByPostId(@Param("postId") long postId);

    // Запрос для получения userId по commentId
    @Query("SELECT l.userId FROM Like l WHERE l.comment.id = :commentId")
    List<Long> findUserIdsByCommentId(@Param("commentId") long commentId);
}
