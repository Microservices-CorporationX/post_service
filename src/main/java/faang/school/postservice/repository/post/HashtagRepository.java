package faang.school.postservice.repository.post;

import faang.school.postservice.model.post.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Query(nativeQuery = true, value = "INSERT INTO post_hashtag (post_id, hashtag_id) VALUES (:postId, :hashtag_id)")
    @Modifying
    void addHashtagToPost(long postId, long hashtag_id);

    Optional<Hashtag> findByName(String name);
}
