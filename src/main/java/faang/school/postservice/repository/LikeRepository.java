package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Stream;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Stream<Like> findAllByPostId(long postId);

    Stream<Like> findAllByCommentId(long postId);

}