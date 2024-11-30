package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UsersCacheMapper;
import faang.school.postservice.model.redis.CacheUser;
import faang.school.postservice.repository.redis.CacheUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final CacheUsersRepository repository;
    private final UsersCacheMapper mapper;
    private final UserServiceClient userClient;
    
    public CacheUser saveCacheUser(Long postAuthorId) {
        UserDto user = userClient.getUser(postAuthorId);
        CacheUser cacheUser = mapper.toCacheUser(user);
        return repository.save(cacheUser);
    }

    public Optional<CacheUser> getCacheUser(Long userId) {
        return repository.findById(userId);
    }
}
