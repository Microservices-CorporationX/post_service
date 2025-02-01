package faang.school.postservice.service.news_feed_service;

import faang.school.postservice.dto.news_feed_models.NewsFeedAuthor;
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
        NewsFeedAuthor newsFeedAuthor = authorCacheMapper.toAuthorCache(author);
        authorCacheRepository.save(newsFeedAuthor);
        log.info("Author cache saved for authorId: {}", authorId);
    }

    public NewsFeedAuthor getAuthorCacheById(Long authorId) {
        return authorCacheRepository.findById(authorId).orElse(null);
    }
}