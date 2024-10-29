package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UsersCacheMapper;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final RedisUsersRepository repository;
    private final UsersCacheMapper mapper;
    private final UserServiceClient userClient;

    public CompletableFuture<Void> saveAllUsersInCache(List<UserDto> allUsers) {
        if (allUsers == null || allUsers.isEmpty()) {
            return completedFuture(null);
        }

        var authorCaches = allUsers.stream()
                .map(mapper::toRedisUser)
                .toList();

        if (!authorCaches.isEmpty()) {
            return runAsync(() -> repository.saveAll(authorCaches));
        }
        return completedFuture(null);
    }

    public void saveRedisUser(Long postAuthorId) {
        UserDto user = userClient.getUser(postAuthorId);
        RedisUser redisUser = mapper.toRedisUser(user);
        repository.save(redisUser);
    }
}
