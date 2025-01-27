package faang.school.postservice.repository.post;

import faang.school.postservice.model.post.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface HashtagRepository extends JpaRepository<Hashtag, UUID> {

    @Query(nativeQuery = true, value = "INSERT INTO post_hashtag (post_id, hashtag_id) VALUES (:postId, :hashtagId)")
    @Modifying
    void addHashtag(long postId, UUID hashtagId);

    Optional<Hashtag> findByName(String name);
}
