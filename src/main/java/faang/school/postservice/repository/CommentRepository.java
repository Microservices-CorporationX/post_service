package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT c FROM Comment c WHERE c.verified = false")
    List<Comment> getAllNotVerified();

    @Modifying
    @Transactional
    @Query("UPDATE Comment set removed = true WHERE verified = false ")
    void markAsRemovedAllUnVerifiedComments();
}
