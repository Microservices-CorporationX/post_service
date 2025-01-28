package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {
    @Query("SELECT l FROM Like l WHERE l.post.id = :postId and l.userId=:userId")
    Optional<Like> findLikeByPostIdAndUserId(long userId, long postId);

    @Query("SELECT l FROM Like l WHERE l.comment.id = :commentId and l.userId=:userId")
    Optional<Like> findLikeByCommentIdAndUserId(long userId, long commentId);
}
