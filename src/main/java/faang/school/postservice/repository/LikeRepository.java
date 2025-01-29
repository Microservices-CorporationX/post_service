package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

    @Query("SELECT l FROM Like l WHERE l.post.id = :postId")
    List<Like> findAllByPostId(long postId);

    @Query("SELECT l FROM Like l WHERE l.comment.id = :commentId")
    List<Like> findAllByCommentId(long commentId);
}
