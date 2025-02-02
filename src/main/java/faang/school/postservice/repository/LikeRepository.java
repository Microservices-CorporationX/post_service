package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);

    Optional<Like> findByCommentIdAndUserId(Long commentId, Long userId);

    void deleteByPostIdAndUserId(Long postId, Long userId);

    void deleteByCommentIdAndUserId(Long postId, Long userId);
}