package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Stream<Like> findAllByPostId(long postId);

    Stream<Like> findAllByCommentId(long postId);

}