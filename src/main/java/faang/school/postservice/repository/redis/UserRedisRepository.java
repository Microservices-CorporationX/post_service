package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.UserRedis;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRedisRepository extends CrudRepository<UserRedis, String> {

//    @Query("SELECT u.followers.id FROM Users u WHERE u.id = :userId")
//    List<Long> getFollowersIdsByUserId(Long userId);

}
