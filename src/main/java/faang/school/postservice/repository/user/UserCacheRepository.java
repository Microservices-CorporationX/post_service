package faang.school.postservice.repository.user;

import faang.school.postservice.model.user.ShortUserWithAvatar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends CrudRepository<ShortUserWithAvatar, Long> {
}
