package faang.school.postservice.redis.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.redis.entity.AuthorCache;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorCacheService {
    private final AuthorCacheRedisRepository authorCacheRedisRepository;
    private final UserServiceClient userServiceClient;
    private final AuthorCacheMapper authorCacheMapper;

    public void saveAuthorCache(long authorId) {
        UserDto author = userServiceClient.getUserById(authorId);
        AuthorCache authorCache = authorCacheMapper.toAuthorCache(author);
        authorCacheRedisRepository.save(authorCache);
    }
}
