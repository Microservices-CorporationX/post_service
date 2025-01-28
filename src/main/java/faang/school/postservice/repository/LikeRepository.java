package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {
    @Query("SELECT l FROM Like l WHERE l.post.id = :postId AND l.userId = :userId")
    Optional<Like> findLikeByPostIdAndUserId(long postId, long userId);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.post.id = :postId AND l.userId = :userId")
    void deleteLikeByPostIdAndUserId(long postId, long userId);

    @Query("SELECT l FROM Like l WHERE l.comment.id = :commentId AND l.userId = :userId")
    Optional<Like> findLikeByCommentIdAndUserId(long commentId, long userId);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.comment.id = :commentId AND l.userId = :userId")
    void deleteLikeByCommentIdAndUserId(long commentId, long userId);
}
