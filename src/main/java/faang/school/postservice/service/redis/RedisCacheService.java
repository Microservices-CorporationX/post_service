package faang.school.postservice.service.redis;

import faang.school.postservice.dto.post.PostRedis;
import faang.school.postservice.dto.user.UserNFDto;

public interface RedisCacheService {
    void savePost(PostRedis post);

    void saveUser(UserNFDto user);
}
