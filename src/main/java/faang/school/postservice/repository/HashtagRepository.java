package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HashtagRepository extends CrudRepository<Hashtag, Long> {

    @Query("SELECT h FROM Hashtag h JOIN h.posts p WHERE p.id = :postId")
    List<Hashtag> findAllByPostId(long postId);
}