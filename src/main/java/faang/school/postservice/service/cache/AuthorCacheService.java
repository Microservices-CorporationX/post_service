package faang.school.postservice.service.cache;

import faang.school.postservice.cache_entities.AuthorCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.user.AuthorCacheMapper;
import faang.school.postservice.repository.cache_repository.AuthorCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorCacheService {
    private final AuthorCacheRepository authorCacheRepository;
    private final AuthorCacheMapper authorCacheMapper;
    private final UserServiceClient userServiceClient;

    public void saveAuthorCache(Long authorId) {
        UserDto author = userServiceClient.getUser(authorId);
        AuthorCache authorCache = authorCacheMapper.toAuthorCache(author);
        authorCacheRepository.save(authorCache);
        log.info("Author cache saved for authorId: {}", authorId);
    }

    public AuthorCache getAuthorCacheById(Long authorId) {
        return authorCacheRepository.findById(authorId).orElse(null);
    }

    public void deleteAuthorCache(Long authorId) {
        authorCacheRepository.deleteById(authorId);
    }
}